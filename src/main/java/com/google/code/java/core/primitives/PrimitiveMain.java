package com.google.code.java.core.primitives;

public class PrimitiveMain {
    public static void main(String... args) throws InterruptedException {
        SimpleIntHashMap counters = new SimpleIntHashMap();
        while (true) {
            performTest(counters);
            Thread.sleep(100);
        }
    }

    private static void performTest(SimpleIntHashMap counters) {
        counters.clear();
        long start = System.nanoTime();
        int runs = 1000 * 1000;
        for (int i = 0; i < runs; i++) {
            int x = i % 1000;
            int y = i / 1000;
            int times = x * y;
            int count = counters.get(times);
            if (count == SimpleIntHashMap.NO_VALUE)
                counters.put(times, 1);
            else
                counters.put(times, count + 1);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per loop%n", time / runs);
    }
}
