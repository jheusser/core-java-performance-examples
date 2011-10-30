/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.files;

import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedWritingMain {
    public static void main(String... args) throws IOException {
        String dir = args[0];
        for (int i = 0; i < 24; i++) {
            long start = System.nanoTime();
            File tmp = new File(dir, "deleteme." + i);
            tmp.deleteOnExit();
            RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
            final MappedByteBuffer map = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 1 << 30);
            IntBuffer array = map.order(ByteOrder.nativeOrder()).asIntBuffer();
            for (int n = 0; n < array.capacity(); n++)
                array.put(n, n);

//      map.force();

            ((DirectBuffer) map).cleaner().clean();
            raf.close();
            long time = System.nanoTime() - start;
            System.out.printf("Took %.1f seconds to write 1 GB%n", time / 1e9);
        }
    }
}
