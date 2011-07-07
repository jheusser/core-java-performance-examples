package com.google.code.java.core.primitives;

import gnu.trove.TIntIntHashMap;

public class Primitive2Main {
    public static void main(String... args) throws InterruptedException {
        TIntIntHashMap counters = new TIntIntHashMap();
        while (true) {
            performTest(counters);
            Thread.sleep(100);
        }
    }

    private static void performTest(TIntIntHashMap counters) {
        counters.clear();
        long start = System.nanoTime();
        int runs = 1000 * 1000;
        for (int i = 0; i < runs; i++) {
            int x = i % 1000;
            int y = i / 1000;
            int times = x * y;
            counters.adjustOrPutValue(times, 1, 1);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per loop%n", time / runs);
    }
}
