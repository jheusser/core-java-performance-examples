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
   http://shootout.alioth.debian.org/

   contributed by Peter Lawrey
*/

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.*;

public class knucleotide {
  static final String ATCG = "ATCG";
  static final int A = 0, T = 1, C = 2, G = 3;
  static final int LONGEST_SEARCH = 18;
  static final long MASK18 = (1L << (2 * LONGEST_SEARCH)) - 1;

  static byte[] values = new byte[256]; static {
    Arrays.fill(values, (byte) -1);
    values['A'] = values['a'] = A;
    values['T'] = values['t'] = T;
    values['C'] = values['c'] = C;
    values['G'] = values['g'] = G;
  }

  private static long encode(String code) {
    long l = 0;
    for (int i = 0; i < code.length(); i++)
      l = (l << 2) | values[code.charAt(i)];
    return l;
  }

  static final long GGT = encode("GGT");
  static final long GGTA = encode("GGTA");
  static final long GGTATT = encode("GGTATT");
  static final long GGTATTTTAATT = encode("GGTATTTTAATT");
  static final long GGTATTTTAATTTATAGT = encode("GGTATTTTAATTTATAGT");

  static int nThreads = 4; //Runtime.getRuntime().availableProcessors();

  public static void main(String... args) throws Exception {
    long start = System.nanoTime();
    FileInputStream in = args.length == 0 ? openStdin() : new FileInputStream(args[0]);
    ExecutorService es = Executors.newFixedThreadPool(nThreads - 1);
    FileChannel fc = in.getChannel();
    ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    int startFrom = findStartOfThree(bb, es, nThreads);
    bb.position(startFrom);

    int blockSize = (bb.remaining() + nThreads - 1) / nThreads;
    for (int i = 0; i < nThreads; i++) {
      int min = startFrom + i * blockSize;
      int max = min + blockSize;
      bb.limit(Math.min(max, bb.capacity()));
      bb.position(min - LONGEST_SEARCH);
      final ByteBuffer bb3 = bb.slice().order(ByteOrder.nativeOrder());
      final Runnable task = new Runnable() {
        @Override
        public void run() {
          new Results().process(bb3, 0, bb3.limit());
        }
      };
      if (i < nThreads - 1)
        es.submit(task);
      else
        task.run();
    }

    in.close();
    es.shutdown();
    es.awaitTermination(1, TimeUnit.MINUTES);
    Results.report();
    long end = System.nanoTime();
    System.out.printf("%nTook %.3f second to count nucleotides%n", (end - start) / 1e9);
  }

  private static FileInputStream openStdin() throws Exception {
    int processId = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    return new FileInputStream("/proc/" + processId + "/fd/0");
  }

  private static int findStartOfThree(ByteBuffer bb, ExecutorService es, int nThreads) throws InterruptedException {
    final ArrayBlockingQueue<Integer> startOfThree = new ArrayBlockingQueue<Integer>(1);
    int blockSize = bb.remaining() / nThreads;
    for (int i = 0; i < nThreads; i++) {
      final int min = i * blockSize;
      final int max = min + blockSize;
      es.submit(new FindThreeRunnable(bb, startOfThree, min, max));
    }
    return startOfThree.take();
  }

  static class Results {
    static final int LEN = 256 * 1024;
    static final int KEY_SIZE = 2 * LONGEST_SEARCH;
    static final long COUNT_BASE = 1L << KEY_SIZE;
    static final long KEY_MASK = COUNT_BASE - 1;
    final long[] keyValues = new long[LEN];
    static final Set<Results> ALL_RESULTS = new CopyOnWriteArraySet<Results>();

    Results() {
      ALL_RESULTS.add(this);
    }

    void increment(long id) {
      if (!tryIncrement(id, id)) return;
      for (int i = 1; i < LEN; i++)
        if (!tryIncrement(id, id + i)) return;
      throw new AssertionError(id);
    }

    private boolean tryIncrement(long id, long id2) {
      int hash = (int) ((id2 + (id2 >>> 17)) & (LEN - 1));
      long key = keyValues[hash];
      if (key == 0) {
        keyValues[hash] = id | COUNT_BASE;
      } else if ((key & KEY_MASK) == id) {
        keyValues[hash] += COUNT_BASE;
      } else {
        return true;
      }
      return false;
    }

    public static void report() {
      int[] count1s = new int[4];
      int[] count2s = new int[4 * 4];
      int[] ggtCounts = new int[5];
      for (Results results : ALL_RESULTS)
        for (long key1 : results.keyValues) {
          if (key1 == 0) continue;
          final long key = key1 & KEY_MASK;
          final int value = (int) (key1 >>> KEY_SIZE);

          count1s[((int) (key & ((1 << 2 * 1) - 1)))] += value;
          count2s[((int) (key & ((1 << 2 * 2) - 1)))] += value;
          if ((key & ((1 << 2 * 3) - 1)) == GGT) ggtCounts[0] += value;
          if ((key & ((1 << 2 * 4) - 1)) == GGTA) ggtCounts[1] += value;
          if ((key & ((1 << 2 * 6) - 1)) == GGTATT) ggtCounts[2] += value;
          if ((key & ((1 << 2 * 12) - 1)) == GGTATTTTAATT) ggtCounts[3] += value;
          if (key == GGTATTTTAATTTATAGT) ggtCounts[4] += value;
        }
      long sum = 0;
      SortedMap<Integer, Integer> singles = new TreeMap<Integer, Integer>(Collections.<Object>reverseOrder());
      for (int i = 0, count1sLength = count1s.length; i < count1sLength; i++) {
        sum += count1s[i];
        singles.put(count1s[i], i);
      }
      for (Map.Entry<Integer, Integer> entry : singles.entrySet())
        System.out.printf("" + ATCG.charAt(entry.getValue()) + " %5.3f%n", 100.0 * entry.getKey() / sum);
      System.out.println();
      SortedMap<Integer, Integer> pairs = new TreeMap<Integer, Integer>(Collections.<Object>reverseOrder());
      for (int i = 0, count2sLength = count2s.length; i < count2sLength; i++)
        pairs.put(count2s[i], i);
      for (Map.Entry<Integer, Integer> entry : pairs.entrySet())
        System.out.printf("" + ATCG.charAt(entry.getValue() / 4) + ATCG.charAt(entry.getValue() % 4) + " %5.3f%n", 100.0 * entry.getKey() / sum);
      System.out.println();
      String[] names = "GGT GGTA GGTATT GGTATTTTAATT GGTATTTTAATTTATAGT".split(" ");
      for (int i = 0; i < ggtCounts.length; i++)
        System.out.printf("%-7d %s%n", ggtCounts[i], names[i]);
    }

    public void process(ByteBuffer bytes, int start, int end) {
      long l = 0;
      for (int i = start; i < start + LONGEST_SEARCH; i++) {
        int b = values[bytes.get(i)];
        if (b < 0) continue;
        l = (l << 2) | b;
      }
      for (int i = start + LONGEST_SEARCH; i < end; i++) {
        int b = values[bytes.get(i)];
        if (b < 0) continue;
        l = (l << 2) | b;
        increment(l & MASK18);
      }
    }
  }

  static class FindThreeRunnable implements Runnable {
    static final int THREE_L = ('>' << 24) | ('T' << 16) | ('H' << 8) | ('R' << 0);
    static final int THREE_B = ('>' << 0) | ('T' << 8) | ('H' << 16) | ('R' << 24);

    static volatile boolean found = false;
    private final ByteBuffer bb;
    private final BlockingQueue<Integer> startOfThree;
    private final int min;
    private final int max;

    public FindThreeRunnable(ByteBuffer bb, BlockingQueue<Integer> startOfThree, int min, int max) {
      this.bb = bb;
      this.startOfThree = startOfThree;
      this.min = min;
      this.max = max;
    }

    @Override
    public void run() {
      try {
        for (int i = min; i < max && !found; i++) {
          switch (bb.getInt(i)) {
            case THREE_B:
            case THREE_L:
              while (bb.get(i++) != '\n') ;
              startOfThree.add(i);
              found = true;
              return;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
