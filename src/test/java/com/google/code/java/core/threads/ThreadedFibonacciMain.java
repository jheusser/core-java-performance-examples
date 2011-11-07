package com.google.code.java.core.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author peter.lawrey
 */
public class ThreadedFibonacciMain {
    static final ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String... args) {
        for (int i = 2; i <= 30; i++) {
            long start = System.nanoTime();
            long n = concurrentFibonacci(i);
            long time = System.nanoTime() - start;
            System.out.printf("Fibonacci %,d was %,d, took %,d us, Time ratio=%.1f %n", i, n, time / 1000, time / 1000.0 / n);
        }
        es.shutdown();
    }

    public static long concurrentFibonacci(int num) {
        Future<Long> ret = es.submit(new FibonacciCallable(num));
        return get(ret);
    }

    static long get(Future<Long> ret) {
        try {
            return ret.get();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    static class FibonacciCallable implements Callable<Long> {
        private final int num;

        public FibonacciCallable(int num) {
            this.num = num;
        }

        @Override
        public Long call() throws Exception {
            if (num < 2) return 1L;
            Future<Long> ret = es.submit(new FibonacciCallable(num - 2));
            // call using the current thread.
            long num1 = new FibonacciCallable(num - 1).call();
            return get(ret) + num1;
        }
    }
}
