package com.google.code.java.core.euler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author peter.lawrey
 */
public class LongestSequenceRecursiveThreadedMain {
    public static void main(String... args) {
        long start = System.nanoTime();
        int n = 1000;
        long[] result = findLongestSequence(n);
        long time = System.nanoTime() - start;
        System.out.printf("%,d has a sequence of length %,d, took %.3f seconds%n", result[1], result[0], time / 1e9);
    }

    private static long[] findLongestSequence(final int minLength) {
        final int[] length = {0};
        final long[] longest = {0};
        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(processors);
        for (int t = 0; t < processors; t++) {
            final int finalT = t;
            es.submit(new Runnable() {
                @Override
                public void run() {
                    long i;
                    for (i = 1 + 2 * finalT; length[0] < minLength; i += 2 * processors) {
                        int length2 = getSequenceLength(i);
                        if (length2 > length[0]) {
                            synchronized (length) {
                                if (length[0] < minLength && length2 > length[0]) {
                                    length[0] = length2;
                                    longest[0] = i;
//                                    System.out.println(length[0] + ": " + longest[0]);
                                }
                            }
                        }
                    }
                    // backtrack as there could be an earlier solution than the one found.
                    synchronized (length) {
                        for (; i < longest[0]; i += 2 * processors) {
                            int length2 = getSequenceLength(i);
                            if (length2 > minLength && i < longest[0]) {
                                length[0] = length2;
                                longest[0] = i;
//                                System.out.println(length[0] + ": " + longest[0]);
                            }
                        }
                    }
                }
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        return new long[]{length[0], longest[0]};
    }

    private static final short[] SEQUENCE_LENGTH_CACHE = new short[1400 * 1000 * 1000];

    private static int getSequenceLength(long n) {
        long i = n;
        if (i <= 2)
            return i == 2 ? 2 : 1;

        if (n < SEQUENCE_LENGTH_CACHE.length) {
            int length0 = SEQUENCE_LENGTH_CACHE[(int) n];
            if (length0 > 0)
                return length0;
        }

        int mod4 = (int) (i & 3);
        if (mod4 == 0) {
            i = i / 4;
        } else if (mod4 == 2) {
            i = (i / 2) * 3 + 1;
        } else {
            i = (i * 3 + 1) / 2;
        }
        int length = 2 + getSequenceLength(i);
        if (n < SEQUENCE_LENGTH_CACHE.length)
            SEQUENCE_LENGTH_CACHE[(int) n] = (short) length;
        return length;
    }
}
