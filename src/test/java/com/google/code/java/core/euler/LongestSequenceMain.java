package com.google.code.java.core.euler;

/**
 * @author peter.lawrey
 */
public class LongestSequenceMain {
    public static void main(String... args) {
        long start = System.nanoTime();
        int n = 700;
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

    private static int getSequenceLength(long i) {
        int count = 1;
        while (i > 2) {
            int mod4 = (int) (i & 3);
            if (mod4 == 0) {
                i = i / 4;
                count += 2;
            } else if (mod4 == 2) {
                i = (i / 2) * 3 + 1;
                count += 2;
            } else {
                i = (i * 3 + 1) / 2;
                count += 2;
            }
        }
        return count + (i == 2 ? 1 : 0);
    }
}
