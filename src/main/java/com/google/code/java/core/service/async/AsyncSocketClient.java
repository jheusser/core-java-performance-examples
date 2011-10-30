/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.service.async;

import com.google.code.java.core.service.api.ByteBufferListener;
import com.google.code.java.core.service.api.Stoppable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class AsyncSocketClient implements Stoppable {
    private final String hostname;
    private final int port;
    private final AsyncConsumer consumer;
    private final ByteBufferListener downStreamListener;
    private final ByteBuffer readingBuffer;

    public AsyncSocketClient(String hostname, int port, int bufferSize, ByteBufferListener downStreamListener) {
        this.hostname = hostname;
        this.port = port;
        this.consumer = new AsyncConsumer(hostname + ':' + port + "-client", bufferSize, new MyByteBufferListener());
        this.downStreamListener = downStreamListener;
        readingBuffer = consumer.createBuffer();
    }

    @Override
    public boolean isStopped() {
        return consumer.isStopped();
    }

    @Override
    public void stop() {
        consumer.stop();
    }

    class MyByteBufferListener implements ByteBufferListener {
        // owned by the async thread.
        private SocketChannel socketChannel = null;

        // called by the async thread.
        @Override
        public void process(ByteBuffer bb) {
            ensureConnected();

            try {
                while (bb.remaining() > 0)
                    socketChannel.write(bb);

            } catch (IOException e) {
                if (!isStopped())
                    e.printStackTrace();
                closeSocketChannel();
            }
        }

        // called by the async thread.
        @Override
        public void processOther() {
            if (socketChannel == null) return;
            try {
                int len = socketChannel.read(readingBuffer);
                if (len < 0) {
                    closeSocketChannel();
                    return;
                } else if (len == 0) {
                    downStreamListener.processOther();
                    return;
                }
                readingBuffer.flip();
                downStreamListener.process(readingBuffer);
                readingBuffer.compact();

            } catch (IOException e) {
                if (!isStopped())
                    e.printStackTrace();
            }
        }

        private void ensureConnected() {
            if (socketChannel != null)
                return;
            while (!isStopped()) {
                try {
                    socketChannel = SocketChannel.open(new InetSocketAddress(hostname, port));
                    socketChannel.configureBlocking(false);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        private void closeSocketChannel() {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException ignored) {
                }
            }
            socketChannel = null;
        }

    }
}
