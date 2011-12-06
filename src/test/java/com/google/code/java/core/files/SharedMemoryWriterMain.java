package com.google.code.java.core.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author peter.lawrey
 */
public class SharedMemoryWriterMain {

    public static final int SIZE = 1000 * 1024 * 1024;

    public static void main(String... args) throws IOException {
        FileChannel fc = new RandomAccessFile("/tmp/deleteme.dat", "rw").getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, SIZE);
        AtomicBoolean flush = new AtomicBoolean();
        while (mbb.remaining() > 0) {
            mbb.putLong(System.nanoTime());
            flush.set(true);
            // pause
            System.nanoTime();
        }
        fc.close();
    }
}
