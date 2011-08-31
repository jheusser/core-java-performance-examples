/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.socket;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Requires Java 7.
 */
public class AsyncPingTest {
  @Test
  public void testSocketWriteReadLatency() throws IOException, InterruptedException, ExecutionException {
    final AsynchronousServerSocketChannel ssc =
        AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 9999));

    AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
    Future<Void> connected = sc.connect(new InetSocketAddress("localhost", 9999));
    Future<AsynchronousSocketChannel> accepted = ssc.accept();
    AsynchronousSocketChannel sc2 = accepted.get();
    configure(sc2);
    close(ssc);
    connected.get();

    ByteBuffer bb = ByteBuffer.allocateDirect(4096);
    ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);

    long times[] = new long[1000 * 1000];
    for (int i = -10000; i < times.length; i++) {
      long start = System.nanoTime();
      bb.position(0);
      bb.limit(1024);
      sc.write(bb);

      bb2.clear();
      sc2.read(bb2).get();

      bb2.flip();
      sc2.write(bb2);

      bb.clear();
      sc.read(bb).get();
      long end = System.nanoTime();
      long err = System.nanoTime() - end;
      long time = end - start - err;
      if (i >= 0)
        times[i] = time;
    }

    close(sc);
    close(sc2);
    Arrays.sort(times);
    System.out.printf("Async Socket latency was 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[times.length / 100] / 1e3,
                         times[times.length / 2] / 1e3,
                         times[times.length - times.length / 100 - 1] / 1e3
    );
  }

  @Test
  public void testSocketWriteReadThroughput() throws IOException, InterruptedException, ExecutionException {
    final AsynchronousServerSocketChannel ssc =
        AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 9999));

    AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
    Future<Void> connected = sc.connect(new InetSocketAddress("localhost", 9999));
    Future<AsynchronousSocketChannel> accepted = ssc.accept();
    AsynchronousSocketChannel sc2 = accepted.get();
    configure(sc2);
    close(ssc);
    connected.get();

    ByteBuffer bb = ByteBuffer.allocateDirect(4096);
    ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);

    long start = System.nanoTime();
    int runs = 1000 * 1000;
    for (int i = 0; i < runs; i++) {
      bb.position(0);
      bb.limit(1024);
      sc.write(bb);

      bb2.clear();
      sc2.read(bb2).get();
      bb2.flip();
      sc2.write(bb2);

      bb.clear();
      sc.read(bb).get();
    }
    long time = System.nanoTime() - start;

    close(sc);
    close(sc2);
    System.out.printf("Async Socket Throughput was %,d K/s%n", runs * 1000000L / time);
  }

  @Test
  public void testSocketLatency() throws IOException, InterruptedException, ExecutionException {
    final AsynchronousServerSocketChannel ssc =
        AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 9999));

    AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
    Future<Void> connected = sc.connect(new InetSocketAddress("localhost", 9999));

    Thread server = new Thread(new Runnable() {
      public void run() {
        AsynchronousSocketChannel sc2 = null;
        try {
          Future<AsynchronousSocketChannel> accepted = ssc.accept();
          sc2 = accepted.get();
          close(ssc);
          ByteBuffer bb1 = ByteBuffer.allocateDirect(4096);
          ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);
          Future<Integer> lastWrite = null;
          ByteBuffer bb = bb1;
          while (!Thread.interrupted()) {
            bb.clear();
            Future<Integer> integerFuture = sc2.read(bb);
            while (!integerFuture.isDone()) {
              if (Thread.interrupted()) return;
            }
            bb.flip();
            // wait for the previous write.
            if (lastWrite != null) lastWrite.get();
            lastWrite = sc2.write(bb);
            // swap the buffers
            bb = (bb == bb1) ? bb2 : bb1;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        } finally {
          close(sc2);
        }
      }
    });
    server.start();

    ByteBuffer bb = ByteBuffer.allocateDirect(4096);
    long times[] = new long[1000 * 1000];

    connected.get();
    for (int i = -10000; i < times.length; i++) {
      long start = System.nanoTime();
      bb.position(0);
      bb.limit(32);
      sc.write(bb).get();


      bb.clear();
      sc.read(bb).get();
      long end = System.nanoTime();
      long err = System.nanoTime() - end;
      long time = end - start - err;
      if (i >= 0)
        times[i] = time;
    }
    server.interrupt();
    close(sc);
    Arrays.sort(times);
    System.out.printf("Threaded Async Socket Latency for 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[times.length / 100] / 1e3,
                         times[times.length / 2] / 1e3,
                         times[times.length - times.length / 100 - 1] / 1e3
    );
  }

  @Test
  public void testSocketThreadedThroughput() throws IOException, InterruptedException, ExecutionException {
    int messageSize = 512;

    final AsynchronousServerSocketChannel ssc =
        AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 9999));

    AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
    Future<Void> connected = sc.connect(new InetSocketAddress("localhost", 9999));

    Thread server = new Thread(new Runnable() {
      public void run() {
        AsynchronousSocketChannel sc2 = null;
        try {
          Future<AsynchronousSocketChannel> accepted = ssc.accept();
          sc2 = accepted.get();
          close(ssc);
          ByteBuffer bb1 = ByteBuffer.allocateDirect(4096);
          ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);
          Future<Integer> lastWrite = null;
          ByteBuffer bb = bb1;
          while (!Thread.interrupted()) {
            bb.clear();
            Future<Integer> integerFuture = sc2.read(bb);
            while (!integerFuture.isDone()) {
              if (Thread.interrupted()) return;
            }
            bb.flip();
            // wait for the previous write.
            if (lastWrite != null) lastWrite.get();
            lastWrite = sc2.write(bb);
            // swap the buffers
            bb = (bb == bb1) ? bb2 : bb1;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        } finally {
          close(sc2);
        }
      }
    });
    server.start();

    ByteBuffer bb1 = ByteBuffer.allocateDirect(4096);
    ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);
    ByteBuffer bb = ByteBuffer.allocateDirect(4096);

    connected.get();

    long start = System.nanoTime();
    int runs = 1000 * 1000;
    Future<Integer> lastRead = null;
    Future<Integer> lastWrite = null;
    for (int i = 0; i < runs; i += 2) {
      bb1.position(0);
      bb1.limit(messageSize);
      if (lastWrite != null) lastWrite.get();
      Future<Integer> write1 = sc.write(bb1);

      bb2.position(0);
      bb2.limit(messageSize);
      write1.get();
      lastWrite = sc.write(bb2);

      if (lastRead != null) lastRead.get();
      bb.clear();
      lastRead = sc.read(bb);
    }
    server.interrupt();
    server.join();
    long time = System.nanoTime() - start;
    close(sc);
    System.out.printf("Threaded Async Socket Throughput was %,d K/s%n", runs * 1000000L / time);
  }

  static void close(Closeable sc2) {
    if (sc2 != null) try {
      sc2.close();
    } catch (IOException ignored) {
    }
  }


  static void configure(AsynchronousSocketChannel sc) throws IOException {
    sc.setOption(StandardSocketOptions.TCP_NODELAY, true);
  }
}
