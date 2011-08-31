/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.math;

import java.math.BigDecimal;

public class RoundingPerformanceMain {
  public static void main(String... args) {
    longCastPerf();
    mathRoundPerf();
    bidDecimalSetScalePerf();
  }

  private static double longCastPerf() {
    double sum = 0; // to avoid micro-optimisation.
    final int runs = 160 * 1000 * 1000; // about 1 second.
    long start = System.nanoTime();
    for (int i = 0; i < runs; i++) {
      double d = i * 0.01;
      sum += roundToTwoPlacesCast(d);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %,d ns for rounding using cast%n", time / runs);
    return sum;
  }

  private static double mathRoundPerf() {
    double sum = 0; // to avoid micro-optimisation.
    final int runs = 50 * 1000 * 1000; // about one second.
    long start = System.nanoTime();
    for (int i = 0; i < runs; i++) {
      double d = i * 0.01;
      sum += roundToTwoPlacesMath(d);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %,d ns for rounding using Math.round%n", time / runs);
    return sum;
  }

  private static double bidDecimalSetScalePerf() {
    double sum = 0; // to avoid micro-optimisation.
    final int runs = 1000 * 1000;  // about 1 second.
    long start = System.nanoTime();
    for (int i = 0; i < runs; i++) {
      double d = i * 0.01;
      sum += roundToTwoPlacesBigDecimal(d);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %,d ns for rounding using BigDecimal.setScale%n", time / runs);
    return sum;
  }

  public static double roundToTwoPlacesCast(double d) {
    return ((long) (d < 0 ? d * 100 - 0.5 : d * 100 + 0.5)) / 100.0;
  }

  public static double roundToTwoPlacesMath(double d) {
    return Math.round(d * 100) / 100.0;
  }

  public static double roundToTwoPlacesBigDecimal(double d) {
    return new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
  }
}
