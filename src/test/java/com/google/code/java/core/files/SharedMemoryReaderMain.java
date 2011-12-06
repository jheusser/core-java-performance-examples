package com.google.code.java.core.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * @author peter.lawrey
 */
public class SharedMemoryReaderMain {
    public static void main(String... args) throws IOException {
        FileChannel fc = new RandomAccessFile("/tmp/deleteme.dat", "rw").getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, SharedMemoryWriterMain.SIZE);
        int[] latencies = new int[SharedMemoryWriterMain.SIZE / 8];
        int count = 0, waitcount = 0;
        while (mbb.remaining() > 0) {
            long l = mbb.getLong(mbb.position());
            if (l == 0) {
                if (++waitcount % (int) 1e9 == 0)
                    System.out.println("read " + count);
                continue;
            }
            long time = mbb.getLong();
            latencies[count++] = (int) (System.nanoTime() - time);
        }
        Arrays.sort(latencies);
        System.out.printf("50/99/99.99%%tile %,d/%,d/%,d%n",
                latencies[count / 2],
                latencies[count - count / 100],
                latencies[count - count / 10000]
        );
        fc.close();
    }
}
