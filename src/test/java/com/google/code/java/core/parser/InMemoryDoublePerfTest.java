/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class InMemoryDoublePerfTest {
  interface PerfTest {
    DoubleReader doubleReader();

    DoubleWriter doubleWriter();

    void finish();

    String toString();
  }

  public void doPerf(PerfTest perfTest) throws IOException, InterruptedException {
    int runs = 1001;
    long[] times = new long[runs];
    double len = 128 * 1024;
    for (int n = 0; n < runs; n++) {
      Thread.sleep(1);
      long start = System.nanoTime();

      DoubleWriter lw = perfTest.doubleWriter();
      lw.write(len);
      for (double i = 0; i < len; i++) {
        lw.write(i * 1000001.0 / 1000000);
      }
      lw.close();

      DoubleReader lr = perfTest.doubleReader();
      double len2 = lr.read();
      if (len != len2)
        assertEquals(len, len2);

      for (double i = 0; i < len; i++) {
        lr.read();
      }
      lr.close();
      times[n] = System.nanoTime() - start;
    }
    perfTest.finish();
    Arrays.sort(times);
    System.out.printf(perfTest + ": Typically took %.1f ns to write/read per double.%n", (double) times[runs / 2] / len);
  }

  @Test
  public void testUnsafePerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      final long address = ParserUtils.UNSAFE.allocateMemory(1024 * 1024);

      @Override
      public DoubleWriter doubleWriter() {
        return new UnsafeDoubleWriter(address);
      }

      @Override
      public DoubleReader doubleReader() {
        return new UnsafeDoubleReader(address);
      }

      @Override
      public String toString() {
        return "Unsafe binary";
      }

      @Override
      public void finish() {
        ParserUtils.UNSAFE.freeMemory(address);
      }
    });
  }

  @Test
  public void testByteBufferPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(1025 * 1024);

      @Override
      public DoubleWriter doubleWriter() {
        buffer.clear();
        return new ByteBufferDoubleWriter(buffer);
      }

      @Override
      public DoubleReader doubleReader() {
        buffer.flip();
        return new ByteBufferDoubleReader(buffer);
      }

      @Override
      public String toString() {
        return "ByteBuffer binary";
      }

      @Override
      public void finish() {
      }
    });
  }

  @Test
  public void testByteBufferTextDirectPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 1024 * 1024);

      @Override
      public DoubleWriter doubleWriter() {
        buffer.clear();
        return new ByteBufferTextDoubleWriter(buffer);
      }

      @Override
      public DoubleReader doubleReader() {
        buffer.flip();
        return new ByteBufferTextDoubleReader(buffer);
      }

      @Override
      public String toString() {
        return "ByteBuffer direct text";
      }

      @Override
      public void finish() {
      }
    });
  }

  @Test
  public void testByteBufferTextPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteBuffer buffer = ByteBuffer.allocate(4 * 1024 * 1024);

      @Override
      public DoubleWriter doubleWriter() {
        buffer.clear();
        return new ByteBufferTextDoubleWriter(buffer);
      }

      @Override
      public DoubleReader doubleReader() {
//                System.out.println(new String(buffer.array(), 0, buffer.position()));
        buffer.flip();
        return new ByteBufferTextDoubleReader(buffer);
      }

      @Override
      public String toString() {
        return "ByteBuffer heap text";
      }

      @Override
      public void finish() {
      }
    });
  }

  @Test
  public void testByteBufferFixedPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteBuffer buffer = ByteBuffer.allocate(2 * 1024 * 1024);

      @Override
      public DoubleWriter doubleWriter() {
        buffer.clear();
        return new ByteBufferFixedWriter(buffer, 9);
      }

      @Override
      public DoubleReader doubleReader() {
//                System.out.println(new String(buffer.array(), 0, buffer.position()));
        buffer.flip();
        return new ByteBufferTextDoubleReader(buffer);
      }

      @Override
      public String toString() {
        return "ByteBuffer heap text";
      }

      @Override
      public void finish() {
      }
    });
  }

/*
    @Test
    public void testUnsafeTextPerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            final double address = ParserUtils.UNSAFE.allocateMemory(1025 * 1024);

            @Override
            public DoubleWriter doubleWriter() {
                return new UnsafeTextDoubleWriter(address);
            }

            @Override
            public DoubleReader doubleReader() {
                return new UnsafeTextDoubleReader(address);
            }

            @Override
            public String toString() {
                return "Unsafe text";
            }

            @Override
            public void finish() {
                ParserUtils.UNSAFE.freeMemory(address);
            }
        });
    }
*/

  @Test
  public void testDataPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteArrayOutputStream baos;

      @Override
      public DoubleWriter doubleWriter() {
        baos = new ByteArrayOutputStream();
        return new DataDoubleWriter(baos);
      }

      @Override
      public DoubleReader doubleReader() {
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return new DataDoubleReader(bais);
      }

      @Override
      public String toString() {
        return "DataStream binary";
      }

      @Override
      public void finish() {
      }
    });
  }

  @Test
  public void testPrintPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteArrayOutputStream baos;

      @Override
      public DoubleWriter doubleWriter() {
        baos = new ByteArrayOutputStream();
        return new PrintDoubleWriter(new PrintWriter(new OutputStreamWriter(baos)));
      }

      @Override
      public DoubleReader doubleReader() {
        return new PrintDoubleReader(
                                        new BufferedReader(
                                                              new InputStreamReader(
                                                                                       new ByteArrayInputStream(baos.toByteArray()))));
      }

      @Override
      public String toString() {
        return "Print text";
      }

      @Override
      public void finish() {
      }
    });
  }

  @Test
  public void testDecimalFormatPerf() throws IOException, InterruptedException {
    doPerf(new PerfTest() {
      ByteArrayOutputStream baos;

      @Override
      public DoubleWriter doubleWriter() {
        baos = new ByteArrayOutputStream();
        return new DecimalFormatDoubleWriter(new PrintWriter(new OutputStreamWriter(baos)));
      }

      @Override
      public DoubleReader doubleReader() {
        return new DecimalFormatDoubleReader(
                                                new BufferedReader(
                                                                      new InputStreamReader(
                                                                                               new ByteArrayInputStream(baos.toByteArray()))));
      }

      @Override
      public String toString() {
        return "DecimalFormat text";
      }

      @Override
      public void finish() {
      }
    });
  }
}
