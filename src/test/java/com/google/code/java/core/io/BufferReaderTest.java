/*
 * Copyright (c) 2012.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class BufferReaderTest {
    public static final int size = 2047 * 1024 * 1024;

    public static void main(String... args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("/d/peter/deleteme.dat", "rw");
        MappedByteBuffer mbb = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
        int[] times = new int[size / 8 / 16];
        int counter = 0;
        int waiting = 0;
        long start = 0;
        while (mbb.remaining() > 0 && counter < times.length) {
            long value = mbb.getLong(mbb.position());
            if ((++waiting & ((1 << 29) - 1)) == 0)
                System.out.println("Counter: " + counter);
            if (value < 0) {
                System.out.println("EOF");
                break;
            } else if (value == 0) {
                continue;
            }
            if (start == 0)
                start = System.nanoTime();

            mbb.position(mbb.position() + 8 * 16);
            times[counter++] = (int) (System.nanoTime() - value);
        }
        long time = System.nanoTime() - start;
        if (mbb.remaining() <= 0)
            System.out.println("end");
        if (counter == times.length)
            System.out.println("remaining: " + mbb.remaining());
        raf.close();
        Arrays.sort(times);
        System.out.printf("0/1/50/99/99.9/99.99%% latency was %,d/%,d/%,d/%,d/%,d/%,d%n",
                times[0],
                times[counter / 99],
                times[counter / 2],
                times[counter - counter / 100],
                times[counter - counter / 1000],
                times[counter - counter / 10000]
        );
        System.out.printf("Throughput %.1f M msg/s%n", counter * 1e3 / time);
    }
}
