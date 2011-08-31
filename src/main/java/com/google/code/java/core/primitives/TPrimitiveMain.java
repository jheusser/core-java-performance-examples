/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.primitives;

import gnu.trove.TIntIntHashMap;

public class TPrimitiveMain {
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
      int count = counters.get(times);
      if (count == 0)
        counters.put(times, 1);
      else
        counters.put(times, count + 1);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took %,d ns per loop%n", time / runs);
  }
}
