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
import java.util.HashMap;
import java.util.Map;

public class PrimitiveHashMapMain {
  private static final long START = System.currentTimeMillis();

  public static void main(String... args) throws InterruptedException, FileNotFoundException {
    int runs = 1200;
    Map<Integer, Integer> counters = new HashMap();
    Report out = new Report(runs);
    System.gc();
    for (int i = 0; i < runs; i++) {
      performTest(out, counters);
      Thread.sleep(100);
    }
    out.print("primitive-hashmap-report.csv");
  }

  private static void performTest(Report out, Map<Integer, Integer> counters) {
    counters.clear();
    long start = System.nanoTime();
    int runs = 300 * 300;
    for (int i = 0; i < runs; i++) {
      int x = i % 300;
      int y = i / 300;
      int times = x * y;
      Integer count = counters.get(times);
      if (count == null)
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
