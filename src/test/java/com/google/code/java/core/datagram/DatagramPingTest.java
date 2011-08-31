/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.datagram;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class DatagramPingTest {

  public static final int LENGTH = 512;

  @Test
  public void testDatagramThroughput() throws IOException {
    DatagramChannel dgc1 = DatagramChannel.open();
    InetSocketAddress addr1 = new InetSocketAddress(18888);
    InetSocketAddress addr2 = new InetSocketAddress(28888);
    dgc1.socket().bind(addr1);
    dgc1.connect(addr2);

    DatagramChannel dgc2 = DatagramChannel.open();
    dgc2.socket().bind(addr2);
    dgc2.connect(addr1);

    ByteBuffer bb1 = ByteBuffer.allocateDirect(LENGTH);
    ByteBuffer bb2 = ByteBuffer.allocateDirect(LENGTH);

    long start = 0;
    int runs = 1000000;
    for (int i = -10000; i < runs; i++) {
      if (i == 0) start = System.nanoTime();
      bb1.clear();
      dgc1.write(bb1);

      bb2.clear();
      int len = dgc2.read(bb2);
      assert len == LENGTH;
      bb2.flip();
      dgc2.write(bb2);

      bb1.clear();
      int len2 = dgc1.read(bb1);
      assert len2 == LENGTH;
//            System.out.println(len+" "+len2);
    }
    long time = System.nanoTime() - start;
    dgc1.close();
    dgc2.close();
    System.out.printf("UDP Pings per second %,d K/s%n", runs * 1000000L / time);
  }

  @Test
  public void testDatagramLatency() throws IOException {
    DatagramChannel dgc1 = DatagramChannel.open();
    InetSocketAddress addr1 = new InetSocketAddress(18888);
    InetSocketAddress addr2 = new InetSocketAddress(28888);
    dgc1.socket().bind(addr1);
    dgc1.connect(addr2);

    DatagramChannel dgc2 = DatagramChannel.open();
    dgc2.socket().bind(addr2);
    dgc2.connect(addr1);

    ByteBuffer bb1 = ByteBuffer.allocateDirect(LENGTH);
    ByteBuffer bb2 = ByteBuffer.allocateDirect(LENGTH);

    int runs = 1000000;
    int[] times = new int[runs];

    for (int i = -10000; i < runs; i++) {
      long start = System.nanoTime();
      bb1.clear();
      dgc1.write(bb1);

      bb2.clear();
      int len = dgc2.read(bb2);
      assert len == LENGTH;
      bb2.flip();
      dgc2.write(bb2);

      bb1.clear();
      int len2 = dgc1.read(bb1);
      assert len2 == LENGTH;
      if (i >= 0)
        times[i] = (int) (System.nanoTime() - start);
    }
    dgc1.close();
    dgc2.close();
    Arrays.sort(times);
    System.out.printf("UDP Pings latency was 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[times.length / 100] / 1e3,
                         times[times.length / 2] / 1e3,
                         times[times.length - times.length / 100 - 1] / 1e3);
  }

  @Test
  public void testThreadedPingThroughput() throws IOException, InterruptedException {
    DatagramChannel dgc1 = DatagramChannel.open();
    InetSocketAddress addr1 = new InetSocketAddress(18888);
    InetSocketAddress addr2 = new InetSocketAddress(28888);
    dgc1.socket().bind(addr1);
    dgc1.connect(addr2);

    final DatagramChannel dgc2 = DatagramChannel.open();
    dgc2.socket().bind(addr2);
    dgc2.connect(addr1);

    Thread server = new Thread(new Runnable() {
      public void run() {
        try {
          ByteBuffer bb2 = ByteBuffer.allocateDirect(LENGTH * 2);
          while (!Thread.interrupted()) {
            bb2.clear();
            dgc2.read(bb2);
            bb2.flip();
            dgc2.write(bb2);
          }
        } catch (ClosedByInterruptException ignored) {
        } catch (ClosedChannelException ignored) {
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    server.start();
    ByteBuffer bb1 = ByteBuffer.allocateDirect(LENGTH);
    int runs = 1000 * 1000;
    long start = 0;
    for (int i = -20000; i < runs; i += 2) {
      if (i == 0) start = System.nanoTime();
      bb1.clear();
      dgc1.write(bb1);

      bb1.clear();
      dgc1.write(bb1);

      bb1.clear();
      int len1 = dgc1.read(bb1);
      assert len1 == LENGTH;

      bb1.clear();
      int len2 = dgc1.read(bb1);
      assert len2 == LENGTH;
    }
    long time = System.nanoTime() - start;
    server.interrupt();
    dgc1.close();
    dgc2.close();

    System.out.printf("Threaded UDP Pings per second %,d K/s%n", runs * 1000000L / time);
  }

  @Test
  public void testThreadedPingLatency() throws IOException, InterruptedException {
    DatagramChannel dgc1 = DatagramChannel.open();
    InetSocketAddress addr1 = new InetSocketAddress(18888);
    InetSocketAddress addr2 = new InetSocketAddress(28888);
    dgc1.socket().bind(addr1);
    dgc1.connect(addr2);

    final DatagramChannel dgc2 = DatagramChannel.open();
    dgc2.socket().bind(addr2);
    dgc2.connect(addr1);

    Thread server = new Thread(new Runnable() {
      public void run() {
        try {
          ByteBuffer bb2 = ByteBuffer.allocateDirect(LENGTH);
          while (!Thread.interrupted()) {
            bb2.clear();
            int len = dgc2.read(bb2);
            assert len == LENGTH;
            bb2.flip();
            dgc2.write(bb2);
          }
        } catch (ClosedByInterruptException ignored) {
        } catch (ClosedChannelException ignored) {
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    server.start();
    ByteBuffer bb1 = ByteBuffer.allocateDirect(LENGTH);
    int runs = 1000 * 1000;
    int times[] = new int[runs];
    for (int i = -10000; i < times.length; i++) {
      long start = System.nanoTime();
      bb1.clear();
      dgc1.write(bb1);

      bb1.clear();
      int len2 = dgc1.read(bb1);
      assert len2 == LENGTH;
      if (i >= 0)
        times[i] = (int) (System.nanoTime() - start);
    }
    server.interrupt();
    dgc1.close();
    dgc2.close();

    Arrays.sort(times);
    System.out.printf("Threaded UDP Pings latency was 1/50/99%%tile %.1f/%.1f/%.1f us%n",
                         times[times.length / 100] / 1e3,
                         times[times.length / 2] / 1e3,
                         times[times.length - times.length / 100 - 1] / 1e3
    );
  }
}
