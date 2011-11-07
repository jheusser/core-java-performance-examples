package com.google.code.java.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author peter.lawrey
 */
public class ByteBufferClient implements ByteBufferPublisher, Closeable {
    public static final int BUFFER_CAPACITY = 64 * 1024;

    private final String hostname;
    private final int port;
    private final ExecutorService reader = Executors.newSingleThreadExecutor();
    private final SocketChannel socketChannel;
    private final ByteBuffer writeBuffer = createBuffer();

    static ByteBuffer createBuffer() {
        return ByteBuffer.allocateDirect(BUFFER_CAPACITY);
    }

    public ByteBufferClient(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;

        socketChannel = SocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(hostname, port));
    }

    public ByteBuffer acquireByteBuffer(int capacity) throws IOException {
        if (capacity > BUFFER_CAPACITY)
            throw new IllegalArgumentException("Capacity " + capacity + " too large.");
        return writeBuffer;
    }

    public void release(ByteBuffer bb) throws IOException {
        while (bb.remaining() > 0)
            socketChannel.write(bb);
    }

    public void close() {
        reader.shutdown();
        try {
            socketChannel.close();
        } catch (IOException ignored) {
        }
    }

    public void consumer(ByteBufferPartialConsumer consumer) {
        reader.submit(new SocketReader(consumer));
    }

    class SocketReader implements Runnable {
        private final ByteBufferPartialConsumer consumer;
        private final ByteBuffer readBuffer = createBuffer();

        public SocketReader(ByteBufferPartialConsumer consumer) {
            this.consumer = consumer;
        }

        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int len = socketChannel.read(readBuffer);
                    if (len < 0)
                        break;
                    readBuffer.flip();
                    consumer.consume(readBuffer);
                    readBuffer.compact();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
