package com.google.code.java.core.threads;

/**
 * @author peter.lawrey
 */
public class UnthreadedFibonacciMain {
    public static void main(String... args) {
        for (int i = 2; i <= 91; i++) {
            long start = System.nanoTime();
            long n = -1;
//            for(int j=0;j<100;j++)
            n = serialFibonacci(i);
            long time = System.nanoTime() - start;
            System.out.printf("Fibonacci %,d was %,d, took %,d us%n", i, n, time / 1000);
        }
    }

    public static long serialFibonacci(int num) {
        long a = 1;
        long b = 1;
        while (num-- >= 2) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }
}
