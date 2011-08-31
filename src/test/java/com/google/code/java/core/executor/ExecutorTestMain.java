/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorTestMain {
  public static void main(String... args) {
    int length = 1000 * 1000;
    final double[] a = generate(length);
    final double[] b = generate(length);
    final double[] c = new double[length];

    int nThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(nThreads);

    for (int i = 0; i < 5; i++) {
      // single threaded
      {
        long start = System.nanoTime();
        geomean(a, b, c, 0, length);
        long time = System.nanoTime() - start;
        System.out.printf("Single threaded: Time to geomean %,d values was %.3f msecs. %n", length, time / 1e6);
      }
      // Too many tasks.
      doTest(length, a, b, c, executor, 1, "Too many tasks");
      // Small number of tasks
      doTest(length, a, b, c, executor, length / nThreads, "One task per thread");
    }
    executor.shutdown();
  }

  private static void doTest(int length, final double[] a, final double[] b, final double[] c, ExecutorService executor, final int blockSize, String desc) {
    List<Future> futures = new ArrayList<Future>();
    long start = System.nanoTime();
    for (int j = 0; j < length; j += blockSize) {
      final int finalJ = j;
      futures.add(executor.submit(new Runnable() {
        @Override
        public void run() {
          geomean(a, b, c, finalJ, finalJ + blockSize);
        }
      }));
    }
    try {
      for (Future future : futures) {
        future.get();
      }
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    long time = System.nanoTime() - start;
    System.out.printf(desc + ": Time to geomean %,d values was %.3f msecs. %n", length, time / 1e6);
  }

  private static double[] generate(int length) {
    double[] d = new double[length];
    for (int i = 0; i < length; i++)
      d[i] = Math.random() - Math.random();
    return d;
  }

  public static void geomean(double[] a, double[] b, double[] c, int from, int to) {
    for (int i = from; i < to; i++)
      c[i] = Math.sqrt(a[i] * b[i]);
  }
}
