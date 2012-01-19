package com.google.code.java.core.euler;

/**
 * @author peter.lawrey
 */
public class LongestSequenceRecursiveMain {
    public static void main(String... args) {
        long start = System.nanoTime();
        int n = 1000;
        long[] result = findLongestSequence(n);
        long time = System.nanoTime() - start;
        System.out.printf("%,d has a sequence of length %,d, took %.3f seconds%n", result[1], result[0], time / 1e9);
    }

    private static long[] findLongestSequence(int minLength) {
        int length = 0;
        long longest = 0;
        for (long i = 1; length < minLength; i += 1) {
            int length2 = getSequenceLength(i);
            if (length2 > length) {
                length = length2;
                longest = i;
                System.out.println(length + ": " + longest);
            }
        }
        return new long[]{length, longest};
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
