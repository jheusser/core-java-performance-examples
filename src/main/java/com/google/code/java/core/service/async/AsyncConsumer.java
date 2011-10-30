/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.service.async;

import com.google.code.java.core.service.api.ByteBufferConsumer;
import com.google.code.java.core.service.api.ByteBufferListener;
import com.google.code.java.core.service.api.Stoppable;
import com.google.code.java.core.service.concurrency.NamedThreadFactory;
import com.google.code.java.core.service.concurrency.PaddedAtomicReference;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncConsumer implements Stoppable, ByteBufferConsumer {
    private final int bufferSize;
    private final ByteBufferListener listener;

    private final ExecutorService service;
    private final AtomicReference<ByteBuffer> bufferRef;

    private volatile boolean stopped = false;

    public AsyncConsumer(String name, int bufferSize, ByteBufferListener listener) {
        service = Executors.newSingleThreadExecutor(new NamedThreadFactory(name, true));
        this.bufferSize = bufferSize;
        this.listener = listener;
        bufferRef = new PaddedAtomicReference<ByteBuffer>(createBuffer());
        service.execute(new Consumer());
    }

    public ByteBuffer acquireByteBuffer(int capacity) {
        while (!stopped) {
            ByteBuffer bb = bufferRef.getAndSet(null);
            if (bb.remaining() >= capacity) {
                return bb;
            }
            // put the buffer back.
            while (!bufferRef.compareAndSet(null, bb)) ;
        }
        throw new IllegalStateException("Stopped");
    }

    public void releaseByteBuffer(ByteBuffer bb) {
        while (!bufferRef.compareAndSet(null, bb)) ;
    }

    public ByteBuffer createBuffer() {
        return ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        stopped = true;
        service.shutdown();
    }

    class Consumer implements Runnable {
        ByteBuffer bb = createBuffer();

        @Override
        public void run() {
            try {
                while (!stopped) {
                    run0();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (!stopped)
                    System.err.println("Consumer dying.");
            }
        }

        private void run0() {
            do {
                bb = bufferRef.getAndSet(bb);
            } while (bb == null);
            if (bb.position() == 0) {
                listener.processOther();
            } else {
                bb.flip();
                listener.process(bb);
                bb.clear();
            }
        }
    }
}
