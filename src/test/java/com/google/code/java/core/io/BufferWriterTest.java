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

public class BufferWriterTest {
    public static final int size = 2047 * 1024 * 1024;
    private static volatile long flush = 0;

    public static void main(String... args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("/tmp/deleteme.dat", "rw");
        MappedByteBuffer mbb = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
        long start = System.nanoTime();
        final long now = System.nanoTime();
        while (mbb.remaining() > 0) {
            mbb.putLong(10);
            for (int i = 0; i < 15; i++)
                mbb.putLong(-2);
            flush = mbb.position();
//            for(int i=0;i<2;i++)
//                System.nanoTime();
        }
        long time = System.nanoTime() - start;
        long count = size / 8 / 16;
        System.out.printf("Throughput %.1f M msg/s%n", count * 1e3 / time);
    }
}
