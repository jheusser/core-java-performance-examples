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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Knucleotide2 {
  static final byte A = 0;
  static final byte T = 1;
  static final byte C = 2;
  static final byte G = 3;

  public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
    long start = System.nanoTime();

    String filename = "/tmp/fasta.txt";
    if (args.length == 1)
      filename = args[0];

    FileInputStream fis = new FileInputStream(filename);
    final ByteBuffer bb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fis.getChannel().size()).order(ByteOrder.nativeOrder());
    int nThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService es = Executors.newFixedThreadPool(nThreads);

    int startFrom = findStartOfThree(bb, es, nThreads);
    int[] count1s = new int[4];
    int[] count2s = new int[4 * 4];
    List<Future> counts = performCounts(bb, es, nThreads, startFrom, count1s, count2s);

    for (Future count : counts) count.get();

    int sum = 0;
    for (int count1 : count1s) sum += count1;
    printCounts(count1s, sum, "A T C G");
    printCounts(count2s, sum, "AA AT AC AG TA TT TC TG CA CT CC CG GA GT GC GG");

    int[] ggtCounts = new int[5];
    bb.position(startFrom);

    performSearches(es, bb, ggtCounts, nThreads);
    es.shutdown();
    es.awaitTermination(1, TimeUnit.MINUTES);

    String[] names = "GGT GGTA GGTATT GGTATTTTAATT GGTATTTTAATTTATAGT".split(" ");
    for (int i = 0; i < names.length; i++)
      System.out.printf("%-9d %s%n", ggtCounts[i], names[i]);
    fis.close();

    long time = System.nanoTime() - start;
    System.out.printf("Took %.3f seconds to run%n", time / 1e9);
  }

  private static void performSearches(ExecutorService es, ByteBuffer bb, int[] ggtCounts, int nThreads) throws ExecutionException, InterruptedException {
    ByteBuffer bb2 = bb.slice().order(ByteOrder.nativeOrder());
    int blockSize = (bb2.limit() + nThreads - 1) / nThreads;
    for (int i = 0; i < nThreads; i++) {
      int min = i * blockSize;
      int max = min + blockSize;
      bb2.limit(Math.min(max + 18, bb2.capacity()));
      bb2.position(min);
      ByteBuffer bb3 = bb2.slice().order(ByteOrder.nativeOrder());
      es.submit(new SearchRunnable(bb3, bb3.limit() - 3, ggtCounts));
    }
  }

  private static boolean expect(ByteBuffer bb2, char ch) {
    byte b;
    b = bb2.get(bb2.position());
    if (b == '\n') {
      bb2.get();
      b = bb2.get(bb2.position());
    }
    if (b != ch & b != ch + 32)
      return true;
    bb2.get();
    return false;
  }

  enum Tester {
    BE_TESTER {
      private final int GGT = ('G' << 16) | ('G' << 8) | ('T' << 0);
      private final int ggt = ('g' << 16) | ('g' << 8) | ('t' << 0);
      private final int GNGT = ('G' << 24) | ('\n' << 16) | ('G' << 8) | ('T' << 0);
      private final int GGNT = ('G' << 24) | ('G' << 16) | ('\n' << 8) | ('T' << 0);
      private final int gngt = ('g' << 24) | ('\n' << 16) | ('g' << 8) | ('t' << 0);
      private final int ggnt = ('g' << 24) | ('g' << 16) | ('\n' << 8) | ('t' << 0);

      public boolean isGGT(int i) {
        switch (i >>> 8) {
          case GGT:
          case ggt:
            return true;
          default:
            return false;
        }
      }

      public boolean isBrokenGGT(int i) {
        switch (i) {
          case GNGT:
          case GGNT:
          case gngt:
          case ggnt:
            return true;
          default:
            return false;
        }
      }
    },
    LE_TESTER {
      private final int GGT = ('G' << 0) | ('G' << 8) | ('T' << 16);
      private final int ggt = ('g' << 0) | ('g' << 8) | ('t' << 16);
      private final int GNGT = ('G' << 0) | ('\n' << 8) | ('G' << 16) | ('T' << 24);
      private final int GGNT = ('G' << 0) | ('G' << 8) | ('\n' << 16) | ('T' << 24);
      private final int gngt = ('g' << 0) | ('\n' << 8) | ('g' << 16) | ('t' << 24);
      private final int ggnt = ('g' << 0) | ('g' << 8) | ('\n' << 16) | ('t' << 24);

      public boolean isGGT(int i) {
        switch (i & 0xFFFFFF) {
          case GGT:
          case ggt:
            return true;
          default:
            return false;
        }
      }

      public boolean isBrokenGGT(int i) {
        switch (i) {
          case GNGT:
          case GGNT:
          case gngt:
          case ggnt:
            return true;
          default:
            return false;
        }
      }
    };

    public static Tester instance() {
      return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? BE_TESTER : LE_TESTER;
    }

    public abstract boolean isGGT(int i);

    public abstract boolean isBrokenGGT(int i);
  }

  private static void printCounts(int[] ints, int sum, String names) {
    String[] nameArr = names.split(" ", -1);
    for (int i = 0; i < ints.length; i++)
      System.out.printf("%s : %.3f%n", nameArr[i], 100.0 * ints[i] / sum);
    System.out.println();
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

  private static List<Future> performCounts(ByteBuffer bb, ExecutorService es, int nThreads, int startFrom, int[] count1s, int[] count2s) throws ExecutionException, InterruptedException {
    int blockSize = bb.remaining() / nThreads;
    List<Future> futures = new ArrayList<Future>();
    for (int i = 0; i < nThreads; i++) {
      final int min = startFrom + i * blockSize;
      final int max = min + blockSize;
      futures.add(es.submit(new Counters(bb, min, max, count1s, count2s)));
    }
    return futures;
  }

  static class Counters implements Runnable {
    private final ByteBuffer bb;
    private final int min;
    private final int max;
    private final int[] count1s;
    private final int[] count2s;

    public Counters(ByteBuffer bb, int min, int max, int[] count1s, int[] count2s) {
      this.bb = bb;
      this.min = min;
      this.max = max;
      this.count1s = count1s;
      this.count2s = count2s;
    }

    @Override
    public void run() {
      int[] count1s = new int[4];
      int[] count2s = new int[5 * 4];
      int max = Math.min(this.max, bb.limit());
      if (min >= max) return;
      int value = 0, count = 0;
      int prev2 = 16;
      for (int i = min; i < max; i++) {
        switch (bb.get(i)) {
          case 'a':
          case 'A':
            value = (value << 2) | A;
            count1s[A]++;
            count2s[prev2 | A]++;
            prev2 = A * 4;
            break;
          case 'g':
          case 'G':
            value = (value << 2) | G;
            count1s[G]++;
            count2s[prev2 | G]++;
            prev2 = G * 4;
            break;
          case 't':
          case 'T':
            value = (value << 2) | T;
            count1s[T]++;
            count2s[prev2 | T]++;
            prev2 = T * 4;
            break;
          case 'c':
          case 'C':
            value = (value << 2) | C;
            count1s[C]++;
            count2s[prev2 | C]++;
            prev2 = C * 4;
            break;
          default:
            count--;
            break;
        }
        if (++count == 4) {
          value = count = 0;
        }
      }
      synchronized (this.count1s) {
        addAll(this.count1s, count1s);
        addAll(this.count2s, count2s);
      }
    }

  }

  static void addAll(int[] to, int[] from) {
    for (int i = 0; i < to.length; i++)
      to[i] += from[i];
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
              i += 6;
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

  private static class SearchRunnable implements Runnable {
    private final ByteBuffer bb2;
    private final int max;
    private final int[] ggtCounts;

    public SearchRunnable(ByteBuffer bb2, int max, int[] ggtCounts) {
      this.bb2 = bb2;
      this.max = max;
      this.ggtCounts = ggtCounts;
    }

    @Override
    public void run() {
      int[] ggtCounts = new int[this.ggtCounts.length];
      Tester tester = Tester.instance();
      while (bb2.position() < max) {
        int bytes = bb2.getInt();
        if (tester.isGGT(bytes)) {
          ggtCounts[0]++;
          bb2.position(bb2.position() - 1);
        } else if (tester.isBrokenGGT(bytes)) {
          ggtCounts[0]++;
        } else {
          bb2.position(bb2.position() - 3);
          continue;
        }
        if (expect(bb2, 'A')) continue;
        ggtCounts[1]++;

        if (expect(bb2, 'T') || expect(bb2, 'T')) continue;

        ggtCounts[2]++;

        if (expect(bb2, 'T')) continue;
        else if (expect(bb2, 'T')) continue;
        else if (expect(bb2, 'A')) continue;
        else if (expect(bb2, 'A')) continue;
        else if (expect(bb2, 'T')) continue;
        else if (expect(bb2, 'T')) continue;

        ggtCounts[3]++;

        if (expect(bb2, 'T') || expect(bb2, 'A') ||
                expect(bb2, 'T') || expect(bb2, 'A') ||
                expect(bb2, 'G') || expect(bb2, 'T')) continue;

        ggtCounts[4]++;
      }
      synchronized (this.ggtCounts) {
        addAll(this.ggtCounts, ggtCounts);
      }
    }
  }
}
