/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.shootout;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

public class Knucleotide3 implements Runnable {
  private static final int A = 0, T = 1, C = 2, G = 3,
      AA = 4, AT = 5, AC = 6, AG = 7,
      TA = 8, TT = 9, TC = 10, TG = 11,
      CA = 12, CT = 13, CC = 14, CG = 15,
      GA = 16, GT = 17, GC = 18, GG = 19,
      GGT = 20, GGTA = 21, GGTATT = 22, GGTATTTTAATT = 23, GGTATTTTAATTTATAGT = 24,
      COUNTS = 25;
  private static final int BUFFER_SIZE = 1024 * 1024;

  private final BlockingQueue<Task> processQueue;
  private final BlockingQueue<Task> returnQueue;
  private final BlockingQueue<Results> resultsQueue;

  public Knucleotide3(BlockingQueue<Task> processQueue,
                      BlockingQueue<Task> returnQueue,
                      BlockingQueue<Results> resultsQueue) {
    this.processQueue = processQueue;
    this.returnQueue = returnQueue;
    this.resultsQueue = resultsQueue;
  }

  public static void main(String... args) throws IOException, InterruptedException {
    long start = System.nanoTime();
    InputStream in = args.length == 0 ? System.in : new FileInputStream(args[0]);
//        in.skip(127000000); // too cheeky?
    int nThreads = Runtime.getRuntime().availableProcessors() * 2;
    BlockingQueue<Task> processQueue = new ArrayBlockingQueue<Task>(nThreads + 1);
    BlockingQueue<Task> returnQueue = new ArrayBlockingQueue<Task>(nThreads + 1);
    BlockingQueue<Results> resultsQueue = new PriorityBlockingQueue<Results>();
    ExecutorService es = Executors.newFixedThreadPool(nThreads);
    final Knucleotide3 kstate = new Knucleotide3(processQueue, returnQueue, resultsQueue);
    for (int i = 0; i < nThreads; i++)
      es.submit(kstate);

    int count = 0, len;
    byte[] overlap = null;
    do {
      final Task task = returnQueue.take();
      final int maxLen;
      if (overlap == null) {
        overlap = new byte[54];
        maxLen = task.bytes.length;
        len = in.read(task.bytes);
        task.len = len - overlap.length;
      } else {
        System.arraycopy(overlap, 0, task.bytes, 0, overlap.length);
        maxLen = task.bytes.length - overlap.length;
        len = task.len = in.read(task.bytes, overlap.length, maxLen);
      }
      if (len == maxLen) {
        System.arraycopy(task.bytes, task.bytes.length - overlap.length, overlap, 0, overlap.length);
      } else {
        if (overlap != null)
          task.len += overlap.length;
        overlap = null;
      }
      if (task.len > 0)
        task.count = count++;
      processQueue.offer(task, 1, TimeUnit.MINUTES);
    } while (len > 0);

    // poison pill remaining tasks.
    for (int i = 0; i < nThreads; i++) {
      final Task task = new Task();
      task.len = 0;
      processQueue.offer(task, 1, TimeUnit.MINUTES);
    }

    Results results = null;
    for (int i = 0; i < count; i++) {
      Results results2 = resultsQueue.take();
      while (results2.resultNumber != i) {
        resultsQueue.add(results2);
        results2 = resultsQueue.take();
      }
      assert results2.resultNumber == i;

      if (results2.foundThree)
        results = results2;
      else if (results != null)
        results.add(results2);
    }
    es.shutdown();
    es.awaitTermination(1, TimeUnit.MINUTES);
    results.report();

    long time = System.nanoTime() - start;
    System.out.printf("Took %.3f seconds to run%n", time / 1e9);
  }

  @Override
  public void run() {

    try {
      returnQueue.add(new Task());
      do {
        Task t = processQueue.take();
        if (t.len <= 0) break;
        Results results = new Results(t.count);
        byte prev = 0;

        for (int i = 0; i < t.len; i++) {
          switch (t.bytes[i]) {
            case '>':
              if (t.bytes[i + 1] == 'T' || t.bytes[i + 1] == 'H' || t.bytes[i + 1] == 'R' || t.bytes[i + 1] == 'E' || t.bytes[i + 1] == 'E') {
                results.clear();
                results.foundThree = true;
                while (t.bytes[++i] != '\n') {
                }
              }
              break;
            case 'A':
            case 'a':
              results.count[A]++;
              results.count[A * 4 + 4 + prev]++;
              prev = A;
              break;
            case 'C':
            case 'c':
              results.count[C]++;
              results.count[C * 4 + 4 + prev]++;
              prev = C;
              break;
            case 'T':
            case 't':
              results.count[T]++;
              results.count[T * 4 + 4 + prev]++;
              prev = T;
              break;
            case 'G':
            case 'g':
              results.count[G]++;
              results.count[G * 4 + 4 + prev]++;
              if (prev == G) {
                scan(results, t.bytes, i + 1);
              }
              prev = G;
              break;
            default:
              break;
          }
        }
        returnQueue.add(t);
        resultsQueue.add(results);
      } while (true);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void scan(Results results, byte[] bytes, int i) {
    try {
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;
      results.count[GGT]++;
      if (bytes[i] == '\n') i++;
      if (bytes[i++] != 'a') return;
      results.count[GGTA]++;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;
      results.count[GGTATT]++;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;

      if (bytes[i] == '\n') i++;
      if (bytes[i] != 'a' & bytes[i++] != 'A') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 'a' & bytes[i++] != 'A') return;

      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;

      results.count[GGTATTTTAATT]++;

      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;

      if (bytes[i] == '\n') i++;
      if (bytes[i] != 'a' & bytes[i++] != 'A') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;

      if (bytes[i] == '\n') i++;
      if (bytes[i] != 'a' & bytes[i++] != 'A') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 'g' & bytes[i++] != 'G') return;
      if (bytes[i] == '\n') i++;
      if (bytes[i] != 't' & bytes[i++] != 'T') return;

      results.count[GGTATTTTAATTTATAGT]++;
    } catch (ArrayIndexOutOfBoundsException ignored) {
    }
  }


  static class Task {
    final byte[] bytes = new byte[BUFFER_SIZE];
    int len;
    int count;
  }

  static class Results implements Comparable<Results> {
    final int resultNumber;
    final int[] count = new int[COUNTS];
    boolean foundThree;

    public Results(int resultNumber) {
      this.resultNumber = resultNumber;
    }

    @Override
    public int compareTo(Results o) {
      return resultNumber - o.resultNumber;
    }

    public void clear() {
      Arrays.fill(count, 0);
    }

    public void add(Results results) {
      for (int i = 0; i < COUNTS; i++)
        count[i] += results.count[i];
    }

    static final Field[] fields = Knucleotide3.class.getDeclaredFields();

    void report() {
      report(A, G + 1);
      System.out.println();
      report(AA, GG + 1);
      System.out.println();
      for (int i = GG + 1; i < COUNTS; i++) {
        System.out.printf("%-7d %s%n", count[i], fields[i].getName());
      }
      System.out.println();
    }

    private void report(int start, int end) {
      List<int[]> toSort = new ArrayList<int[]>();
      int sum = 0;
      for (int i = start; i < end; i++) {
        toSort.add(new int[]{i, count[i]});
        sum += count[i];
      }
      Collections.sort(toSort, new Comparator<int[]>() {
        @Override
        public int compare(int[] o1, int[] o2) {
          return o2[1] - o1[1];
        }
      });
      for (int[] line : toSort) {
        System.out.printf("%s %.3f%n", fields[line[0]].getName(), 100.0 * line[1] / sum);
      }
    }
  }
}
