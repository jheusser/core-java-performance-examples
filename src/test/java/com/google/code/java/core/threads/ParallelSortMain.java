/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.threads;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelSortMain {
  public static void main(String... args) throws InterruptedException {
    Random rand = new Random();
    final int[] values = new int[100 * 1024 * 1024];
    for (int i = 0; i < values.length; i++)
      values[i] = rand.nextInt();

    int threads = Runtime.getRuntime().availableProcessors();
    ExecutorService es = Executors.newFixedThreadPool(threads);
    int blockSize = (values.length + threads - 1) / threads;
    for (int i = 0; i < values.length; i += blockSize) {
      final int min = i;
      final int max = Math.min(min + blockSize, values.length);
      es.submit(new Runnable() {
        @Override
        public void run() {
          Arrays.sort(values, min, max);
        }
      });
    }
    es.shutdown();
    es.awaitTermination(10, TimeUnit.MINUTES);
    for (int blockSize2 = blockSize; blockSize2 < values.length / 2; blockSize2 *= 2) {
      for (int i = 0; i < values.length; i += blockSize2) {
        final int min = i;
        final int mid = Math.min(min + blockSize2, values.length);
        final int max = Math.min(min + blockSize2 * 2, values.length);
        mergeSort(values, min, mid, max);
      }
    }
  }

  private static boolean mergeSort(int[] values, int left, int mid, int end) {
    int[] results = new int[end - left];
    int l = left, r = mid, m = 0;
    for (; l < left && r < mid; m++) {
      int lv = values[l];
      int rv = values[r];
      if (lv < rv) {
        results[m] = lv;
        l++;
      } else {
        results[m] = rv;
        r++;
      }
    }
    while (l < mid)
      results[m++] = values[l++];
    while (r < end)
      results[m++] = values[r++];
    System.arraycopy(results, 0, values, left, results.length);
    return false;
  }
}
