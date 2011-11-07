package com.google.code.java.core.io;

import com.google.code.java.core.time.HiresTimer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author peter.lawrey
 */
public class SimplePerf2Main {
    public static void main(String... args) throws IOException {
        for (int i = 0; i < 10; i++)
            test();
    }

    public static void test() throws IOException {
        long start = HiresTimer.nanoTime();
        ByteBuffer bb = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
        final int count = 100 * 1000 * 1000;
        for (long i = 0; i < count; i += 10) {
            for (int j = 0; j < 10; j++) {
                bb.putLong(i);
                bb.putLong(i);
                bb.flip();
                while (bb.remaining() >= 8) {
                    if (i != bb.getLong())
                        throw new AssertionError();
                }
                bb.clear();
            }
            long now = HiresTimer.nanoTime();
            if (now < start) break;
        }
        double avgTime = (double) (HiresTimer.nanoTime() - start) / count;
        System.out.printf("Average time %.1f ns%n", avgTime);
    }
}
