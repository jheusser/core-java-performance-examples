/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.shootout;

/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * contributed by Stefan Krause
 * slightly modified by Chad Whipkey
 * parallelized by Colin D Bennett 2008-10-04
 * reduce synchronization cost by The Anh Tran
 * optimizations and refactoring by Enotus 2010-11-11
 */
// run with java  -server -XX:+TieredCompilation -XX:+AggressiveOpts mandelbrot 32000

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public final class mandelbrot {
  static byte[][] out;
  static AtomicInteger yCt;
  static double[] Crb;
  static double[] Cib;

  static int getByte(int x, int y) {
    double Ci = Cib[y];
    int res = 0;
    for (int i = 0; i < 8; i += 2) {
      double Zr1 = Crb[x + i];
      double Zi1 = Cib[y];

      double Zr2 = Crb[x + i + 1];
      double Zi2 = Cib[y];

      int b = 0;
      int j = 49;
      do {
        double nZr1 = Zr1 * Zr1 - Zi1 * Zi1 + Crb[x + i];
        double nZi1 = Zr1 * Zi1 + Zr1 * Zi1 + Cib[y];
        Zr1 = nZr1;
        Zi1 = nZi1;

        double nZr2 = Zr2 * Zr2 - Zi2 * Zi2 + Crb[x + i + 1];
        double nZi2 = Zr2 * Zi2 + Zr2 * Zi2 + Cib[y];
        Zr2 = nZr2;
        Zi2 = nZi2;

        if (Zr1 * Zr1 + Zi1 * Zi1 > 4) b |= 2;
        if (Zr2 * Zr2 + Zi2 * Zi2 > 4) b |= 1;
        if (b == 3) break;
      } while (--j > 0);
      res = (res << 2) + b;
    }
    return ~res;
  }

  static void putLine(int y, byte[] line) {
    for (int xb = 0; xb < line.length; xb++)
      line[xb] = (byte) getByte(xb * 8, y);
  }

  public static void main(String[] args) throws Exception {
    int N = 6000;
    if (args.length >= 1) N = Integer.parseInt(args[0]);

    long start = System.nanoTime();
    Crb = new double[N + 7];
    Cib = new double[N + 7];
    double invN = 2.0 / N;
    for (int i = 0; i < N; i++) {
      Cib[i] = i * invN - 1.0;
      Crb[i] = i * invN - 1.5;
    }
    yCt = new AtomicInteger();
    out = new byte[N][(N + 7) / 8];

    Thread[] pool = new Thread[2 * Runtime.getRuntime().availableProcessors()];
    for (int i = 0; i < pool.length; i++)
      pool[i] = new Thread() {
        public void run() {
          int y;
          while ((y = yCt.getAndIncrement()) < out.length) putLine(y, out[y]);
        }
      };
    for (Thread t : pool) t.start();
    for (Thread t : pool) t.join();
    long time = System.nanoTime() - start;
    System.out.printf("Took %.3f seconds to run.%n", time / 1e9);

    OutputStream stream = new BufferedOutputStream(new FileOutputStream("/tmp/mandelbrot.out"));
    stream.write(("P4\n" + N + " " + N + "\n").getBytes());
    for (int i = 0; i < N; i++) stream.write(out[i]);
    stream.close();
  }
}
