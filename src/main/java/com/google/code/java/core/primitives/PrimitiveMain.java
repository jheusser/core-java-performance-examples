/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.primitives;

import java.io.FileNotFoundException;

public class PrimitiveMain {
  private static final long START = System.currentTimeMillis();

  public static void main(String... args) throws InterruptedException, FileNotFoundException {
    int runs = 1200;
    SimpleIntHashMap counters = new SimpleIntHashMap();
    Report out = new Report(runs);
    System.gc();
    for (int i = 0; i < runs; i++) {
      performTest(out, counters);
      Thread.sleep(100);
    }
    out.print("primitive-report.csv");
  }

  private static void performTest(Report out, SimpleIntHashMap counters) {
    counters.clear();
    long start = System.nanoTime();
    int runs = 300 * 300;
    for (int i = 0; i < runs; i++) {
      int x = i % 300;
      int y = i / 300;
      int times = x * y;
      int count = counters.get(times);
      if (count == SimpleIntHashMap.NO_VALUE)
        counters.put(times, 1);
      else
        counters.put(times, count + 1);
    }
    long time = System.nanoTime() - start;
    out.usedMB[out.count] = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1e6;
    out.timeFromStart[out.count] = (System.currentTimeMillis() - START) / 1e3;
    out.avgTime[out.count] = (double) time / runs;
    out.count++;
  }
}
