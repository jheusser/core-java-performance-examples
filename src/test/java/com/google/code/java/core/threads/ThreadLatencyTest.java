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

import java.util.Arrays;

public class ThreadLatencyTest {
  enum Pauser {
    YIELD {
      @Override
      public void pause() {
        Thread.yield();
      }
    }, NO_WAIT {
      @Override
      public void pause() {
        // nothing.
      }
    }, BUSY_WAIT_10 {
      @Override
      public void pause() {
        long start = System.nanoTime();
        while (System.nanoTime() - start < 10e6) ;
      }
    }, BUSY_WAIT_3 {
      @Override
      public void pause() {
        long start = System.nanoTime();
        while (System.nanoTime() - start < 3e6) ;
      }
    }, BUSY_WAIT_1 {
      @Override
      public void pause() {
        long start = System.nanoTime();
        while (System.nanoTime() - start < 1e6) ;
      }
    }, SLEEP_10 {
      @Override
      public void pause() throws InterruptedException {
        Thread.sleep(10);
      }
    }, SLEEP_3 {
      @Override
      public void pause() throws InterruptedException {
        Thread.sleep(3);
      }
    }, SLEEP_1 {
      @Override
      public void pause() throws InterruptedException {
        Thread.sleep(1);
      }
    };

    public abstract void pause() throws InterruptedException;
  }

  @Test
  public void testLatency() throws InterruptedException {
    System.out.print("Warmup - ");
    doTest(Pauser.NO_WAIT);

    for (Pauser pauser : Pauser.values())
      doTest(pauser);
  }

  private void doTest(Pauser delay) throws InterruptedException {
    int[] times = new int[1000 * 1000];
    byte[] bytes = new byte[32 * 1024];
    byte[] bytes2 = new byte[32 * 1024];
    long end = System.nanoTime() + (long) 5e9;
    int i;
    for (i = 0; i < times.length; i++) {
      long start = System.nanoTime();
      System.arraycopy(bytes, 0, bytes2, 0, bytes.length);
      long time = System.nanoTime() - start;
      times[i] = (int) time;
      delay.pause();
      if (start > end) break;
    }
    Arrays.sort(times, 0, i);
    System.out.printf(delay + ": Copy memory latency 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[i / 100] / 1e3,
                         times[i / 2] / 1e3,
                         times[i - i / 100 - 1] / 1e3
    );
  }
}
