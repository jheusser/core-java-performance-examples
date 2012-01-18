package com.google.code.java.core.euler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author peter.lawrey
 */
public class LongestSequenceThreadedMain {
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
                    int localLongest = 0;
                    long i;
                    for (i = 1 + 2 * finalT; length[0] < minLength; i += 2 * processors) {
                        int length2 = getSequenceLength(i);
                        if (length2 > localLongest) {
                            synchronized (length) {
                                if (length2 > length[0]) {
                                    length[0] = length2;
                                    longest[0] = i;
                                    System.out.println(length[0] + ": " + longest[0]);
                                }
                                localLongest = length[0];
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
                                System.out.println(length[0] + ": " + longest[0]);
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

    private static int getSequenceLength(long i) {
        int count = 1;
        while (i > 2) {
            long mod4 = i & 3;
            // multiple of 4.
            if (mod4 == 0) {
                i = i / 4;
                count += 2;

                // multiple of 2 but not 4.
            } else if (mod4 == 2) {
                i = i / 2 * 3 + 1;
                count += 2;

                // odd number.
            } else {
                i = (i * 3 + 1) / 2;
                count += 2;
            }
        }
        return count + (i == 2 ? 1 : 0);
    }
}
