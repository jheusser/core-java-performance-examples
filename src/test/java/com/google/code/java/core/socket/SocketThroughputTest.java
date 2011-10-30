/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.socket;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/*
Java 6 update 26
With 1 clients, transfer rate of 1,332 MB/s
With 2 clients, transfer rate of 2,218 MB/s
With 4 clients, transfer rate of 3,522 MB/s
With 8 clients, transfer rate of 4,538 MB/s
With 16 clients, transfer rate of 4,344 MB/s
With 32 clients, transfer rate of 4,086 MB/s
With 100 clients, transfer rate of 3,823 MB/s
With 300 clients, transfer rate of 3,906 MB/s
With 1,000 clients, transfer rate of 3,898 MB/s

Java 7 update 1
With 1 clients, transfer rate of 1,064 MB/s
With 2 clients, transfer rate of 2,132 MB/s
With 4 clients, transfer rate of 3,390 MB/s
With 8 clients, transfer rate of 3,945 MB/s
With 16 clients, transfer rate of 4,078 MB/s
With 32 clients, transfer rate of 4,055 MB/s
With 100 clients, transfer rate of 3,965 MB/s
With 300 clients, transfer rate of 3,892 MB/s
With 1,000 clients, transfer rate of 3,913 MB/s
 */

public class SocketThroughputTest {
    @Test
    public void testThroughput() throws IOException, InterruptedException {
        for (int clients : new int[]{1, 2, 4, 8, 16, 32, 100, 300, 1000, 2500, 5000, 10000}) {
            doTest(clients, 64 * 1024, 8L * 1024 * 1024 * 1024);
        }
    }

    public void doTest(final int clients, final int blockSize, final long size) throws IOException, InterruptedException {
        long start = System.nanoTime();
//        EchoService echo = new EchoService(12345);
        ExecutorService es = Executors.newCachedThreadPool();
        final AtomicLong size2 = new AtomicLong();
        for (int i = 0; i < clients; i++) {
            final SocketChannel sc = SocketChannel.open(new InetSocketAddress("localhost", 12345));
            es.execute(new Runnable() {
                @Override
                public void run() {
                    ByteBuffer bb = ByteBuffer.allocateDirect(blockSize);
                    try {
                        for (long i = 0; i < size; i += blockSize * clients) {
                            bb.clear();
                            while (bb.remaining() > 0) sc.write(bb);

                            bb.clear();
                            while (bb.remaining() > 0) sc.read(bb);
                            size2.addAndGet(blockSize);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            sc.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);

//        echo.stop();
        long time = System.nanoTime() - start;
        long rate = size2.get() * 1000000L / 1024 / 1024 * 1000 / time;
        System.out.printf("With %,d clients, transfer rate of %,d MB/s%n", clients, rate);
    }
}
