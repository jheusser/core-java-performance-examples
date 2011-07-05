package com.google.code.java.core.primitives;

import java.util.HashMap;
import java.util.Map;

public class WrapperMain {
    public static void main(String... args) throws InterruptedException {
        while (true) {
            performTest();
            Thread.sleep(100);
        }
    }

    private static void performTest() {
        long start = System.nanoTime();
        Map<Integer, Integer> counters = new HashMap<Integer, Integer>();
        int runs = 20 * 1000;
        for (Integer i = 0; i < runs; i++) {
            Integer x = i % 12;
            Integer y = i / 12 % 12;
            Integer times = x * y;
            Integer count = counters.get(times);
            if (count == null)
                counters.put(times, 1);
            else
                counters.put(times, count + 1);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per loop%n", time / runs);
    }
}
