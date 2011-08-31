/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.threads;

import java.util.ArrayList;
import java.util.List;

public class MaxThreadsMain {

  public static final int BATCH_SIZE = 4000;

  public static void main(String... args) throws InterruptedException {
    List<Thread> threads = new ArrayList<Thread>();
    try {
      for (int i = 0; i <= 100 * 1000; i += BATCH_SIZE) {
        long start = System.currentTimeMillis();
        addThread(threads, BATCH_SIZE);
        long end = System.currentTimeMillis();
        Thread.sleep(1000);
        long delay = end - start;
        System.out.printf("%,d threads: Time to create %,d threads was %.3f seconds %n", threads.size(), BATCH_SIZE, delay / 1e3);
      }
    } catch (Throwable e) {
      System.err.printf("After creating %,d threads, ", threads.size());
      e.printStackTrace();
    }

  }

  private static void addThread(List<Thread> threads, int num) {
    for (int i = 0; i < num; i++) {
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            while (!Thread.interrupted()) {
              Thread.sleep(1000);
            }
          } catch (InterruptedException ignored) {
            //
          }
        }
      });
      t.setDaemon(true);
      t.setPriority(Thread.MIN_PRIORITY);
      threads.add(t);
      t.start();
    }
  }
}
