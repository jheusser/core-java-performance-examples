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
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncConsumerTest {
    @Test
    public void testAsyncConsumer() throws InterruptedException {
        doAsyncTest(128);
        for (int bs = 128; bs <= 1024 * 1024; bs *= 2)
            doAsyncTest(bs);
    }

    private static void doAsyncTest(int bufferSize) throws InterruptedException {
        final int runs = 100000000;
        final int[] times = new int[runs];
        final AtomicInteger lastRead = new AtomicInteger();
        ByteBufferListener listener = new ByteBufferListener() {
            @Override
            public void process(ByteBuffer bb) {
                int i = -1;
                while (bb.remaining() > 0) {
                    i = bb.getInt();
                    long timeNS = bb.getLong();
                    long time = System.nanoTime() - timeNS;
                    times[i] = (int) time;
                }
                if (i > 0)
                    lastRead.set(i);
            }

            @Override
            public void processOther() {
            }
        };
        AsyncConsumer ac = new AsyncConsumer("ac", bufferSize, listener);
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            final ByteBuffer bb = ac.acquireByteBuffer(4 + 8);
            bb.putInt(i);
            bb.putLong(System.nanoTime());
            ac.releaseByteBuffer(bb);
        }
        while (lastRead.get() + 1 < runs) ;

        long time = System.nanoTime() - start;
        Arrays.sort(times);
        System.out.printf("%,d buffer: Throughput %,d msg/s, ", bufferSize, (long) (runs * 1e9 / time));
        System.out.printf("Latency %.1f / %.1f /%.1f μs for 50 / 99 / 99.99th percentile%n",
                times[runs / 2] / 1e3,
                times[runs - runs / 100] / 1e3,
                times[runs - runs / 10000] / 1e3
        );
        ac.stop();
        Thread.yield();
    }
}
