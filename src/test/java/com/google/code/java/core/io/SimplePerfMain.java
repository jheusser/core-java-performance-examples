package com.google.code.java.core.io;

import com.google.code.java.core.time.HiresTimer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author peter.lawrey
 */
public class SimplePerfMain {
    public static void main(String... args) throws IOException {
        for (int i = 0; i < 10; i++)
            test();
    }

    public static void test() throws IOException {

        long start = HiresTimer.nanoTime();
        long end = start + 1000 * 1000 * 1000;
        ByteBuffer bb = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
        long now;
        long count = 0;
        final ByteBufferPartialConsumer reader = new MyByteBufferPartialConsumer();
        long expected = 0;
        do {
            now = HiresTimer.nanoTime();
            for (int i = 0; i < 100; i++) {
                bb.putLong(count);

                bb.flip();
//                reader.consume(bb);
//                if (bb.remaining() >= 8) {
                if (i != -1) {
                    long id = bb.getLong();
                    if (id != expected)
                        throw new AssertionError();
                    expected++;
                }

                bb.clear();
                count++;
            }
        } while (now < end);
        double avgTime = (double) (HiresTimer.nanoTime() - start) / count;
        System.out.printf("Average time %.1f ns%n", avgTime);
    }

    private static class MyByteBufferPartialConsumer implements ByteBufferPartialConsumer {
        long expected = 0;

        public void consume(ByteBuffer bb) {
            while (bb.remaining() >= 16) {
                long id = bb.getLong();
                if (id != expected)
                    throw new AssertionError();
                expected++;
                long sentTime = bb.getLong();
            }
        }
    }
}
