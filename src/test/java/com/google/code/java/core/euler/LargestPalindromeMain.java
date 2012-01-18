package com.google.code.java.core.euler;

import java.util.Arrays;

/**
 * @author peter.lawrey
 */
// 99956644665999
public class LargestPalindromeMain {
    public static void main(String... args) {
        long start = System.nanoTime();
        for (int digits = 2; digits < 9; digits++) {
            long[] solution = findLargestPalindromeProductForDigits(digits);
            System.out.printf("The largest palindrome which is the product of two %d digit numbers is%n" +
                    "%,d * %,d = %,d%n", digits, solution[0], solution[1], solution[2]);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Solutions took %.3f seconds to find.%n", time / 1e9);
    }

    private static long[] findLargestPalindromeProductForDigits(int n) {
        long max = TENS[n] - 1;
        long min = TENS[n - 1];

        for (long i = max; i > min; i--) {
            for (long j = max; j >= i; j--) {
                long product = i * j;
//                System.out.println(product);
                if (isPalidrome(product)) {
                    long[] ret = {i, j, product};
                    return ret;
                }
            }
        }
        throw new AssertionError("Unable to find a solution");
    }

    private static long[] TENS = new long[19];

    static {
        TENS[0] = 1;
        for (int i = 1; i < TENS.length; i++)
            TENS[i] = TENS[i - 1] * 10;
    }

    private static long tens(long number) {
        int tens = Arrays.binarySearch(TENS, number);
        return TENS[tens < 0 ? ~tens - 1 : tens];
    }

    private static boolean isPalidrome(long number) {
        long hiTens = tens(number);
        long lowTens = 1;
        while (hiTens > lowTens) {
            long hiDigit = number / hiTens % 10;
            long lowDigit = number / lowTens % 10;
            if (hiDigit != lowDigit)
                return false;
            hiTens /= 10;
            lowTens *= 10;
        }
        return true;
    }
}
