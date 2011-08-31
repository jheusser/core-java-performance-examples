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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;

public class knucleotideB {
  static final int A = 0, T = 1, C = 2, G = 3;
  static final long MASK18 = (1L << (2 * 18)) - 1;

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

  static final int BUFFER_REUSE = 32; // bytes. The min is 18-1+1.
  static int nThreads = 4; //Runtime.getRuntime().availableProcessors();

  public static void main(String... args) throws IOException, InterruptedException {
    long start = System.nanoTime();
    FileInputStream in = args.length == 0 ? openStdin() : new FileInputStream(args[0]);
    final FileChannel fc = in.getChannel();
    ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    ExecutorService es = Executors.newFixedThreadPool(nThreads);
    searchForThree(bb, es);
    int len;
    long mid = System.nanoTime();

/*
    Task task = Task.FREE_LIST.take();
    while ((len = in.read(task.bytes, BUFFER_REUSE, Task.BUFFER_SIZE - BUFFER_REUSE)) > 0) {
      System.arraycopy(bufferReuse, 0, task.bytes, 0, BUFFER_REUSE);
      task.start = 0;
      task.end = len + BUFFER_REUSE;
      es.submit(task);
      System.arraycopy(task.bytes, len, bufferReuse, 0, BUFFER_REUSE);
      task = Task.FREE_LIST.take();
    }
*/
    in.close();
    es.shutdown();
    es.awaitTermination(1, TimeUnit.MINUTES);
    Results.report();
    long end = System.nanoTime();
    System.out.printf("1) Took %.3f second to find THREE from stdin%n", (mid - start) / 1e9);
    System.out.printf("2) Took %.3f second to count nucleotides%n", (end - mid) / 1e9);
  }

  private static FileInputStream openStdin() throws FileNotFoundException {
    int processId = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    return new FileInputStream("/proc/" + processId + "/fd/0");
  }

  private static void searchForThree(ByteBuffer bb, ExecutorService es) throws InterruptedException, IOException {
    Task task = Task.FREE_LIST.take();
    int i;
    OUTER:
    for (i = 0; i < bb.limit(); i++)
      if (bb.get(i) == '>' && bb.get(i + 1) == 'T' && bb.get(i + 2) == 'H' && bb.get(i + 3) == 'R' && bb.get(i + 4) == 'E' && bb.get(i + 5) == 'E')
        break OUTER;
    while (bb.get(++i) != '\n') ;
    task.start = i - BUFFER_REUSE;
    task.end = bb.limit();
    es.submit(task);
  }

  static class Task implements Runnable {
    static final ArrayBlockingQueue<Task> FREE_LIST = new ArrayBlockingQueue<Task>(nThreads + 1);

    static {
      for (int i = 0; i <= nThreads; i++) FREE_LIST.add(new Task());
    }

    ByteBuffer bytes;
    int start, end;

    @Override
    public void run() {
      assert !FREE_LIST.contains(this);
      Results.LOCAL_RESULTS.get().process(bytes, start, end);
      FREE_LIST.add(this);
    }
  }

  static class Results {
    static final int LEN = 256 * 1024;
    static final int KEY_SIZE = 2 * 18;
    static final long COUNT_BASE = 1L << KEY_SIZE;
    static final long KEY_MASK = COUNT_BASE - 1;
    final long[] keyValues = new long[LEN];
    static final Set<Results> ALL_RESULTS = new CopyOnWriteArraySet<Results>();
    static final ThreadLocal<Results> LOCAL_RESULTS = new ThreadLocal<Results>() {
      @Override
      protected Results initialValue() {
        return new Results();
      }
    };

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
      for (int i = 0, count1sLength = count1s.length; i < count1sLength; i++) {
        System.out.println(i + " : " + count1s[i]);
      }
      System.out.println();
      for (int i = 0, count2sLength = count2s.length; i < count2sLength; i++) {
        System.out.println(i + " : " + count2s[i]);
      }
      System.out.println();
      for (int i = 0; i < ggtCounts.length; i++) {
        System.out.println(i + " : " + ggtCounts[i]);
      }
    }

    public void process(ByteBuffer bb, int start, int end) {
      long l = 0;
      for (int i = start; i < start + BUFFER_REUSE; i++) {
        int b = values[bb.get(i)];
        if (b < 0) continue;
        l = (l << 2) | b;
      }
      for (int i = start + BUFFER_REUSE; i < end; i++) {
        int b = values[bb.get(i)];
        if (b < 0) continue;
        l = (l << 2) | b;
        increment(l & MASK18);
      }
    }
  }
}
