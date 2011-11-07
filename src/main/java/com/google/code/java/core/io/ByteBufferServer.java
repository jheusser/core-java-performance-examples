package com.google.code.java.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.code.java.core.io.ByteBufferClient.createBuffer;

/**
 * @author peter.lawrey
 */
public class ByteBufferServer implements Closeable {
    private final ServerSocketChannel serverSocketChannel;
    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Factory<ByteBufferProcessor> consumerFactory;

    public ByteBufferServer(int port, Factory<ByteBufferProcessor> consumerFactory) throws IOException {
        this.consumerFactory = consumerFactory;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        service.execute(new Acceptor());
    }

    public void close() {
        service.shutdown();
        try {
            serverSocketChannel.close();
        } catch (IOException ignored) {
        }
    }

    class Acceptor implements Runnable {
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SocketChannel accept = serverSocketChannel.accept();
                    service.submit(new ServerHandle(accept, consumerFactory.create()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }

    class ServerHandle implements Runnable, ByteBufferPublisher {
        private final SocketChannel socketChannel;
        private final ByteBufferProcessor byteBufferProcessor;
        private final ByteBuffer readBuffer = createBuffer();
        private final ByteBuffer writeBuffer = createBuffer();

        public ServerHandle(SocketChannel socketChannel, ByteBufferProcessor byteBufferProcessor) {
            this.socketChannel = socketChannel;
            this.byteBufferProcessor = byteBufferProcessor;
            byteBufferProcessor.out(this);
        }

        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (socketChannel.read(readBuffer) < 0) break;
                    readBuffer.flip();
                    byteBufferProcessor.consume(readBuffer);
                    readBuffer.compact();
                    if (readBuffer.remaining() < 1)
                        throw new IllegalStateException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeSession();
            }
        }

        private void closeSession() {
            try {
                socketChannel.close();
            } catch (IOException ignored) {
            }
        }

        public ByteBuffer acquireByteBuffer(int capacity) throws IOException {
            if (writeBuffer.remaining() < capacity)
                throw new IllegalStateException();
            return writeBuffer;
        }

        public void release(ByteBuffer bb) throws IOException {
            bb.flip();
            while (bb.remaining() > 0)
                socketChannel.write(bb);
            bb.clear();
        }
    }
}
