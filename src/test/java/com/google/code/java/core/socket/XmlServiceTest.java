/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.socket;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;

public class XmlServiceTest {
  @Test
  public void xmlServiceRoundTrip() throws IOException {
    HeartbeatServer hs = new HeartbeatServer(9999);
    hs.start();
    int runs = 3 * 1000 * 1000;

    final AtomicInteger count = new AtomicInteger();
    final long[] times = new long[runs];
    EventListener el = new EventListener() {
      @Override
      public void heartbeatRequest(long timestamp, long sequenceNumber) {
        // ignore
      }

      @Override
      public void heartbeatResponse(long timestamp, long sequenceNumber) {
        int n = count.getAndIncrement();
        times[n] = System.nanoTime() - sequenceNumber;
      }

      @Override
      public void error(String message) {
        System.err.println("client: " + message);
      }
    };
    SocketChannel sc = SocketChannel.open(new InetSocketAddress(9999));
    ByteBuffer wb = ByteBuffer.allocateDirect(4 * 1024);
    XmlEventParser xep = new XmlEventParser();
    for (int i = 0; i < runs; i += 2) {
      writeHeartbeatRequest(sc, wb, System.nanoTime());
      writeHeartbeatRequest(sc, wb, System.nanoTime());
      ByteBuffer rb = xep.bufferForWriting(128);
      sc.read(rb);
      xep.process(el);
    }
    // read remaining responses.
    while (count.get() < runs) {
      ByteBuffer rb = xep.bufferForWriting(128);
      sc.read(rb);
      xep.process(el);
    }
    hs.stop();
    Arrays.sort(times);
    System.out.printf("Socket latency was 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[times.length / 100] / 1e3,
                         times[times.length / 2] / 1e3,
                         times[times.length - times.length / 100 - 1] / 1e3
    );

  }

  @Test
  public void xmlServiceThroughput() throws IOException {
    HeartbeatServer hs = new HeartbeatServer(9999);
    hs.start();
    int runs = 3 * 1000 * 1000;

    final AtomicInteger count = new AtomicInteger();
    EventListener el = new EventListener() {
      @Override
      public void heartbeatRequest(long timestamp, long sequenceNumber) {
        // ignore
      }

      @Override
      public void heartbeatResponse(long timestamp, long sequenceNumber) {
        count.getAndIncrement();
      }

      @Override
      public void error(String message) {
        System.err.println("client: " + message);
      }
    };
    SocketChannel sc = SocketChannel.open(new InetSocketAddress(9999));
    ByteBuffer wb = ByteBuffer.allocateDirect(64 * 1024);
    XmlEventParser xep = new XmlEventParser();
    long start = System.nanoTime();
    for (int i = 0; i < runs; i += 2) {
      writeHeartbeatRequest(sc, wb, System.nanoTime());
      writeHeartbeatRequest(sc, wb, System.nanoTime());
      ByteBuffer rb = xep.bufferForWriting(8 * 1024);
      sc.read(rb);
      xep.process(el);
    }
    // read remaining responses.
    while (count.get() < runs) {
      ByteBuffer rb = xep.bufferForWriting(8 * 1024);
      sc.read(rb);
      xep.process(el);
    }
    long time = System.nanoTime() - start;
    hs.stop();
    System.out.printf("Socket throughput was %,d K/s%n", (long) (runs * 1e6 / time));

  }

  static class HeartbeatServer implements Runnable {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ServerSocketChannel ssc;

    HeartbeatServer(int port) throws IOException {
      ssc = ServerSocketChannel.open();
      try {
        ssc.socket().bind(new InetSocketAddress(port));
      } catch (IOException e) {
        close(ssc);
        throw e;
      }
    }


    public void start() {
      executor.submit(this);
    }

    public void stop() {
      close(ssc);
      executor.shutdown();
    }

    @Override
    public void run() {
      try {
        while (!Thread.interrupted()) {
          SocketChannel sc = ssc.accept();
          executor.submit(new HeartbeatHandler(sc));
        }
      } catch (ClosedChannelException ignored) {
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    class HeartbeatHandler implements Runnable, EventListener {
      private final SocketChannel sc;
      private final ByteBuffer wb = ByteBuffer.allocateDirect(64 * 1024);

      public HeartbeatHandler(SocketChannel sc) {
        this.sc = sc;
      }

      @Override
      public void run() {
        XmlEventParser parser = new XmlEventParser();
        try {
          while (true) {
            ByteBuffer bb = parser.bufferForWriting(8 * 1024);
            sc.read(bb);
            parser.process(this);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void heartbeatRequest(long timestamp, long sequenceNumber) {
        try {
          writeHeartbeatResponse(sc, wb, sequenceNumber);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void heartbeatResponse(long timestamp, long sequenceNumber) {
        // ignore.
      }

      @Override
      public void error(String message) {
        System.err.println("server: " + message);
      }
    }

  }

  static interface EventListener {
    void heartbeatRequest(long timestamp, long sequenceNumber);

    void heartbeatResponse(long timestamp, long sequenceNumber);

    void error(String message);
  }

  static class XmlEventParser {
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(64 * 1024); {
      buffer.order(ByteOrder.nativeOrder());
    }

    private int readOffset = 0;
    private int writeOffset = 0;
    private XmlState state = XmlState.ROOT;
    private long timestamp = 0;

    public ByteBuffer bufferForWriting(int capacity) {
      if (writeOffset + capacity > buffer.capacity()) {
        if (buffer.position() == 0) throw new IllegalArgumentException("Capacity too large");
        buffer.limit(writeOffset);
        buffer.position(readOffset);
        buffer.compact();
        writeOffset -= readOffset;
        readOffset = 0;
      }
      buffer.limit(buffer.capacity());
      buffer.position(writeOffset);
      return buffer;
    }

    public void process(EventListener listener) {
      // save write position.
      writeOffset = buffer.position();
      // reset for read.
      buffer.limit(writeOffset);
      buffer.position(readOffset);
      int position = readOffset;

      try {
        while (state.requires() <= buffer.remaining()) {
          state = state.process(this, buffer, listener);
          position = buffer.position();
        }
      } catch (BufferUnderflowException e) {
        // needs to read more data.
      }
      readOffset = position;

    }

    /*
   <Root timestamp="{timestamp}"><HeartbeatRequest sequence="{sequence}"/></Root>
   or
   <Root timestamp="{timestamp}"><HeartbeatResponse sequence="{sequence}"/></Root>
    */
    private static final int ROOT = ('<' << 24) + ('R' << 16) + ('o' << 8) + ('o' << 0);
    private static final int ROOT2 = ('<' << 0) + ('R' << 8) + ('o' << 16) + ('o' << 24);

    enum XmlState {
      ROOT {
        private final int requires = "<Root timestamp=\"1\">".length();
        private final int ROOT_TAIL_LENGTH = "t timestamp=\"".length();

        @Override
        int requires() {
          return requires;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          int word = bb.getInt();
          switch (word) {
            case XmlEventParser.ROOT:
            case XmlEventParser.ROOT2:
              int length = ROOT_TAIL_LENGTH;
              windForward(bb, length);
              return READ_TIMESTAMP;
            default:
              listener.error("Invalid start of XML message, ignoring.");
              bb.position(bb.position() - 3);
              return CONSUME_UNTIL_ROOT;
          }
        }
      },
      READ_TIMESTAMP {
        private final int requires = "1\">".length();
        private final int END_LENGTH = ">".length();

        @Override
        int requires() {
          return requires;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          xep.timestamp = readLong(bb);
          windForward(bb, END_LENGTH);
          return REQUEST;
        }
      },
      REQUEST {
        // the shortest complete request
        private final int requires = "<HeartbeatRequest sequence=\"1\"/>".length();
        private final int SKIP = "<HeartbeatRe".length();
        private final int REQUEST_SKIP = "uest sequence=\"".length();
        private final int RESPONSE_SKIP = "ponse sequence=\"".length();

        @Override
        int requires() {
          return requires;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          windForward(bb, SKIP);
          switch (bb.get()) {
            case 'q':
              windForward(bb, REQUEST_SKIP);
              return HEARTBEAT_REQUEST;
            case 's':
              windForward(bb, RESPONSE_SKIP);
              return HEARTBEAT_RESPONSE;
            default:
              listener.error("Invalid request type, ignoring.");
              bb.position(bb.position() - 3);
              return CONSUME_UNTIL_ROOT;
          }
        }
      },
      HEARTBEAT_REQUEST {
        private final int requires = "1\"/>".length();
        private final int END_LENGTH = "/></Root>\n".length();

        @Override
        int requires() {
          return requires;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          long sequence = readLong(bb);
          windForward(bb, END_LENGTH);
          listener.heartbeatRequest(xep.timestamp, sequence);
          return ROOT;
        }
      },
      HEARTBEAT_RESPONSE {
        private final int requires = "1\">".length();
        private final int END_LENGTH = "/></Root>\n".length();

        @Override
        int requires() {
          return requires;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          long sequence = readLong(bb);
          windForward(bb, END_LENGTH);
          listener.heartbeatResponse(xep.timestamp, sequence);
          return ROOT;
        }
      },
      CONSUME_UNTIL_ROOT {
        @Override
        int requires() {
          return 4;
        }

        @Override
        XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener) {
          int word = bb.getInt();
          switch (word) {
            case XmlEventParser.ROOT:
            case XmlEventParser.ROOT2:
              bb.position(bb.position() - 4);
              return READ_TIMESTAMP;
            default:
              bb.position(bb.position() - 3);
              return CONSUME_UNTIL_ROOT;
          }
        }
      };


      static void windForward(ByteBuffer bb, int length) {
        if (length > bb.remaining()) throw new BufferUnderflowException();
        bb.position(bb.position() + length);
      }

      abstract int requires();

      abstract XmlState process(XmlEventParser xep, ByteBuffer bb, EventListener listener);
    }
  }

  public static long readLong(ByteBuffer bb) {
    long value = 0;
    boolean negative = false;
    while (true) {
      byte b = bb.get();
      if (b >= '0' && b <= '9') {
        value = value * 10 + b - '0';
      } else if (b == '-') {
        negative = true;
      } else {
        break;
      }
    }
    return negative ? -value : value;
  }

  static final byte[] ROOT_BYTES = "<Root timestamp=\"".getBytes();
  static final byte[] REQUEST_BYTES = "\"><HeartbeatRequest sequence=\"".getBytes();
  static final byte[] RESPONSE_BYTES = "\"><HeartbeatResponse sequence=\"".getBytes();
  static final byte[] END_ROOT_BYTES = "\"/></Root>\n".getBytes();

  static void writeHeartbeatRequest(SocketChannel sc, ByteBuffer wb, long sequenceNumber) throws IOException {
    wb.clear();
    wb.put(ROOT_BYTES);
    writeLong(wb, System.nanoTime());
    wb.put(REQUEST_BYTES);
    writeLong(wb, sequenceNumber);
    wb.put(END_ROOT_BYTES);
    wb.flip();
    sc.write(wb);
  }

  static void writeHeartbeatResponse(SocketChannel sc, ByteBuffer wb, long sequenceNumber) throws IOException {
    wb.clear();
    wb.put(ROOT_BYTES);
    writeLong(wb, System.nanoTime());
    wb.put(RESPONSE_BYTES);
    writeLong(wb, sequenceNumber);
    wb.put(END_ROOT_BYTES);
    wb.flip();
    sc.write(wb);
  }

  private static final byte[] MIN_VALUE_BYTES = Long.toString(Long.MIN_VALUE).getBytes();

  public static void writeLong(ByteBuffer wb, long l) {
    if (l == Long.MIN_VALUE) {
      wb.put(MIN_VALUE_BYTES);
    } else if (l == 0) {
      wb.put((byte) '0');
    } else {
      if (l < 0) {
        wb.put((byte) '-');
        l = -l;
      }
      long tens = tensFloor(l);
      while (tens > 0) {
        wb.put((byte) (l / tens % 10 + '0'));
        tens /= 10;
      }
    }
  }

  static final long[] TENS = new long[19]; static {
    TENS[0] = 1;
    for (int i = 1; i < TENS.length; i++) TENS[i] = TENS[i - 1] * 10;
  }

  static long tensFloor(long l) {
    int idx = Arrays.binarySearch(TENS, l);
    return idx < 0 ? TENS[~idx - 1] : TENS[idx];
  }

  public static void close(Closeable closeable) {
    if (closeable != null) try {
      closeable.close();
    } catch (IOException ignored) {
    }
  }

  @Test
  public void testWriteReadLong() {
    ByteBuffer bb = ByteBuffer.allocate(4 * 1024);
    for (long l = Long.MIN_VALUE; l < 0; l /= 2) {
      writeLong(bb, l);
      bb.put((byte) '\n');
      writeLong(bb, ~l);
      bb.put((byte) '\n');
    }
//        System.out.println(new String(bb.array(), 0, bb.position()));
    bb.flip();
    for (long l = Long.MIN_VALUE; l < 0; l /= 2) {
      assertEquals(l, readLong(bb));
      assertEquals(~l, readLong(bb));
    }
  }

  @Test
  public void testXmlEventParser() {
    Mockery mock = new Mockery();
    final EventListener el = mock.mock(EventListener.class);
    mock.checking(new Expectations() {{
      oneOf(el).heartbeatRequest(123456789, 1);
    }});

    XmlEventParser xep = new XmlEventParser();
    ByteBuffer bb = xep.bufferForWriting(256);
    bb.put("<Root timestamp=\"123456789\"><HeartbeatRequest sequence=\"1\"/></Root>\n".getBytes());
    xep.process(el);
    mock.assertIsSatisfied();

    mock.checking(new Expectations() {{
      oneOf(el).heartbeatResponse(123456790, 2);
    }});
    bb = xep.bufferForWriting(256);
    bb.put("<Root timestamp=\"123456790\"><HeartbeatResponse sequence=\"2\"/></Root>\n".getBytes());
    xep.process(el);
    mock.assertIsSatisfied();
  }
}
