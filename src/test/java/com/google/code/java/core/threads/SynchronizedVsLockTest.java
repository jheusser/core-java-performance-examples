/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.threads;

import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedVsLockTest {
  static final Object mutex1 = new Object();
  static final Object mutex2 = new Object();
  static final Lock lock1 = new ReentrantLock();
  static final Lock lock2 = new ReentrantLock();
  static int counter1 = 0;
  static int counter2 = 0;
  static final AtomicInteger counter3 = new AtomicInteger();
  static final AtomicInteger counter4 = new AtomicInteger();
  public static final int LOOPS = 50 * 1000 * 1000;
  static final Map<Integer, double[]> results = new TreeMap<Integer, double[]>();

  @Test
  public void testSvL() throws InterruptedException {
    testSvL1();
    testSvL2();

    for (Map.Entry<Integer, double[]> entry : results.entrySet()) {
      System.out.print("<tr><td align=\"right\">");
      System.out.print(entry.getKey());
      for (double v : entry.getValue()) {
        System.out.print("</td><td>");
        System.out.printf("%.3f", v);
      }
      System.out.println("</td></tr>");
    }
  }

  static void testSvL1() throws InterruptedException {
    for (final int t : new int[]{1, 2, 4, 8, 16, 32, 64}) {
      doTest(t, 0, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            synchronized (mutex1) {
              counter1++;
            }
          }
        }

        @Override
        public String toString() {
          return "1x synchronized {}";
        }
      });
      doTest(t, 1, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            lock1.lock();
            try {
              counter1++;
            } finally {
              lock1.unlock();
            }
          }
        }

        @Override
        public String toString() {
          return "1x Lock.lock()/unlock()";
        }
      });
      doTest(t, 2, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            counter3.getAndIncrement();
          }
        }

        @Override
        public String toString() {
          return "1x AtomicInteger";
        }
      });
    }
  }

  static void testSvL2() throws InterruptedException {
    for (final int t : new int[]{1, 2, 4, 8, 16, 32, 64}) {
      doTest(t, 3, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            synchronized (mutex1) {
              counter1++;
            }
            synchronized (mutex2) {
              counter2++;
            }
          }
        }

        @Override
        public String toString() {
          return "2x synchronized {}";
        }
      });
      doTest(t, 4, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            lock1.lock();
            try {
              counter1++;
            } finally {
              lock1.unlock();
            }
            lock2.lock();
            try {
              counter2++;
            } finally {
              lock2.unlock();
            }
          }
        }

        @Override
        public String toString() {
          return "2x Lock.lock()/unlock()";
        }
      });
      doTest(t, 5, new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < LOOPS / t; i++) {
            counter3.getAndIncrement();
            counter4.getAndIncrement();
          }
        }

        @Override
        public String toString() {
          return "2x AtomicInteger";
        }
      });
    }
  }

  private static void doTest(int threads, int testNum, Runnable runnable) throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(threads);
    long start = System.nanoTime();
    try {
      for (int i = 0; i < threads; i++)
        es.execute(runnable.getClass().getDeclaredConstructor(int.class).newInstance(threads));

    } catch (Exception e) {
      throw new AssertionError(e);
    } finally {
      es.shutdown();
    }
    es.awaitTermination(1, TimeUnit.MINUTES);
    long time = (System.nanoTime() - start) / 1000000;
    System.out.printf("%s with %d threads took %.3f seconds%n", runnable.toString(), threads, time / 1e3);
    double[] times = results.get(threads);
    if (times == null)
      results.put(threads, times = new double[6]);
    times[testNum] = time / 1e3;
  }
}
