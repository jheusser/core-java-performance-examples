package com.google.code.java.core.euler;

/**
 * @author peter.lawrey
 */
public class SumEvenFibonacciMain {
    public static void main(String... args) {
        long sum = largestLongSumOfEven();
        System.out.println("sum= " + sum);
    }

    private static long largestLongSumOfEven() {
        long sum = 0, x = 1, y = 1;
        do {
            long z = x + y;
            // detect an overflow.
            long nextSum = sum + z;
            if ((long) (double) nextSum != nextSum)
                break;
            sum = nextSum;
            x = y + z;
            y = z + x;
        } while (true);
        return sum;
    }
}
