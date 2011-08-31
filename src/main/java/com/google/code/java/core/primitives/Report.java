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
import java.io.PrintWriter;

public class Report {
  final double[] usedMB, timeFromStart, avgTime;
  int count;

  public Report(int capacity) {
    usedMB = new double[capacity];
    timeFromStart = new double[capacity];
    avgTime = new double[capacity];
  }

  void print(String filename) throws FileNotFoundException {
    PrintWriter out = new PrintWriter(filename);
    for (int i = 0; i < count; i++)
      out.printf("%.1f, Took %.1f ns per loop, free MB, %.1f%n", timeFromStart[i], avgTime[i], usedMB[i]);
    out.close();

  }
}
