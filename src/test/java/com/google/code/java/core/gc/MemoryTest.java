/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.gc;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static junit.framework.Assert.assertEquals;

public class MemoryTest {

  public static final long MEMORY_TESTED = 24 * 1024 * 1024 * 1024L;

  @Test
  public void hugeUnsafeMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long address = UNSAFE.allocateMemory(length);
    long time2 = System.nanoTime() - start2;
    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      huWrite(length, address);

      long mid = System.nanoTime();
      System.out.println("reading");
      huRead(length, address);
      long end = System.nanoTime();
      System.out.printf("Unsafe bytes took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      UNSAFE.freeMemory(address);
      t.interrupt();
    }
  }

  private static void huWrite(long length, long address) {
    for (long l = 0; l < length; l++, address++)
      UNSAFE.putByte(address, (byte) l);
  }

  private static void huRead(long length, long address) {
    for (long l = 0; l < length; l++, address++) {
      byte b = UNSAFE.getByte(address);
      if (b != (byte) l)
        assertEquals((byte) l, b);
    }
  }

  @Test
  public void hugeUnsafeUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long address = UNSAFE.allocateMemory(length);
    long time2 = System.nanoTime() - start2;
    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      huuWrite(length, address);

      long mid = System.nanoTime();
      System.out.println("reading");
      huuRead(length, address);
      long end = System.nanoTime();
      System.out.printf("Unsafe bytes unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      UNSAFE.freeMemory(address);
      t.interrupt();
    }
  }

  private static void huuWrite(long length, long address) {
    for (long l = 0; l < length; l += 8, address += 8) {
      UNSAFE.putByte(address, (byte) l);
      UNSAFE.putByte(address + 1, (byte) (l + 1));
      UNSAFE.putByte(address + 2, (byte) (l + 2));
      UNSAFE.putByte(address + 3, (byte) (l + 3));
      UNSAFE.putByte(address + 4, (byte) (l + 4));
      UNSAFE.putByte(address + 5, (byte) (l + 5));
      UNSAFE.putByte(address + 6, (byte) (l + 6));
      UNSAFE.putByte(address + 7, (byte) (l + 7));
    }
  }

  private static void huuRead(long length, long address) {
    for (long l = 0; l < length; l += 8, address += 8) {
      byte b = UNSAFE.getByte(address);
      byte b1 = UNSAFE.getByte(address + 1);
      byte b2 = UNSAFE.getByte(address + 2);
      byte b3 = UNSAFE.getByte(address + 3);
      byte b4 = UNSAFE.getByte(address + 4);
      byte b5 = UNSAFE.getByte(address + 5);
      byte b6 = UNSAFE.getByte(address + 6);
      byte b7 = UNSAFE.getByte(address + 7);
      if (b != (byte) l)
        assertEquals((byte) l, b);
      if (b1 != (byte) (l + 1))
        assertEquals((byte) (l + 1), b);
      if (b2 != (byte) (l + 2))
        assertEquals((byte) (l + 2), b);
      if (b3 != (byte) (l + 3))
        assertEquals((byte) (l + 3), b);
      if (b4 != (byte) (l + 4))
        assertEquals((byte) (l + 4), b);
      if (b5 != (byte) (l + 5))
        assertEquals((byte) (l + 5), b);
      if (b6 != (byte) (l + 6))
        assertEquals((byte) (l + 6), b);
      if (b7 != (byte) (l + 7))
        assertEquals((byte) (l + 7), b);
    }
  }

  @Test
  public void hugeUnsafeLongMemoryPerformance() throws InterruptedException {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long address = UNSAFE.allocateMemory(length / 2);
    long address2 = UNSAFE.allocateMemory(length / 2);
    long time2 = System.nanoTime() - start2;
    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      int count = 0;
      for (long l = 0; l < length / 2; l += 1024 * 1024) {
        luWrite(1024 * 1024, address + l);
        luWrite(1024 * 1024, address2 + l);
        if (l > 20 * 1024 * 1024 * 1024L)
          Thread.sleep(20);
        count += 2;
        if (count % 10 == 0) System.out.println(count);
      }

      long mid = System.nanoTime();
      System.out.println("reading");
      count = 0;
      for (long l = 0; l < length / 2; l += 1024 * 1024) {
        luRead(1024 * 1024, address + l);
        luRead(1024 * 1024, address2 + l);
        Thread.sleep(20);
        count += 2;
        if (count % 10 == 0) System.out.println(count);
      }
      long end = System.nanoTime();
      System.out.printf("Unsafe long Took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      UNSAFE.freeMemory(address);
      t.interrupt();
    }
  }

  private void luWrite(long length, long address) {
    for (long l = 0; l < length; l += 8, address += 8)
      UNSAFE.putLong(address, l);
  }

  private static void luRead(long length, long address) {
    for (long l = 0; l < length; l += 8, address += 8) {
      long l2 = UNSAFE.getLong(address);
      if (l != l2)
        assertEquals(l, l2);
    }
  }

  @Test
  public void hugeUnsafeLongUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long address = UNSAFE.allocateMemory(length);
    long time2 = System.nanoTime() - start2;
    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      luuWrite(length, address);

      long mid = System.nanoTime();
      System.out.println("reading");
      luuRead(length, address);
      long end = System.nanoTime();
      System.out.printf("Unsafe long unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      UNSAFE.freeMemory(address);
      t.interrupt();
    }
  }

  private static void luuWrite(long length, long address) {
    for (long l = 0; l < length - 24; l += 32, address += 32) {
      UNSAFE.putLong(address, l);
      UNSAFE.putLong(address + 8, l + 8);
      UNSAFE.putLong(address + 16, l + 16);
      UNSAFE.putLong(address + 24, l + 24);
    }
  }

  private static void luuRead(long length, long address) {
    for (long l = 0; l < length - 24; l += 32, address += 32) {
      long l0 = UNSAFE.getLong(address);
      long l1 = UNSAFE.getLong(address + 8);
      long l2 = UNSAFE.getLong(address + 16);
      long l3 = UNSAFE.getLong(address + 24);
      if (l != l0)
        assertEquals(l, l0);
      if (l + 8 != l1)
        assertEquals(l + 8, l1);
      if (l + 16 != l2)
        assertEquals(l + 16, l2);
      if (l + 24 != l3)
        assertEquals(l + 24, l3);
    }
  }

  private static Thread monitorThread() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        long start = System.currentTimeMillis();
        while (!Thread.interrupted()) {
          System.out.println((System.currentTimeMillis() - start) / 1000
                                 + " sec - " +
                                 (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024
                                 + " KB used");
          try {
            Thread.sleep(10000);
          } catch (InterruptedException ignored) {
            break;
          }
        }
      }
    });
    t.setDaemon(true);
    t.start();
    return t;
  }

  @Test
  public void hugeByteBufferMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    ByteBuffer[] buffers = new ByteBuffer[16];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = ByteBuffer.allocateDirect((int) (length / 16)).order(ByteOrder.nativeOrder());
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      bbWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      bbRead(buffers);
      long end = System.nanoTime();
      System.out.printf("ByteBuffer byte took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private void bbWrite(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j++) {
        buffer.put((byte) j);
      }
    }
  }

  private void bbRead(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      buffer.flip();
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j++) {
        byte b = buffer.get();
        if (b != (byte) j)
          assertEquals((byte) j, b);
      }
    }
  }

  @Test
  public void hugeByteBufferUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    ByteBuffer[] buffers = new ByteBuffer[16];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = ByteBuffer.allocateDirect((int) (length / 16)).order(ByteOrder.nativeOrder());
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      bbuWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      bbuRead(buffers);
      long end = System.nanoTime();
      System.out.printf("ByteBuffer byte unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void bbuWrite(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 4) {
        buffer.put(j, (byte) j);
        buffer.put(j + 1, (byte) (j + 1));
        buffer.put(j + 2, (byte) (j + 2));
        buffer.put(j + 3, (byte) (j + 3));
      }
    }
  }

  private static void bbuRead(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 4) {
        byte b = buffer.get(j);
        byte b1 = buffer.get(j + 1);
        byte b2 = buffer.get(j + 2);
        byte b3 = buffer.get(j + 3);
        if (b != (byte) j)
          assertEquals((byte) j, b);
        if (b1 != (byte) (j + 1))
          assertEquals((byte) (j + 1), b1);
        if (b2 != (byte) (j + 2))
          assertEquals((byte) (j + 2), b2);
        if (b3 != (byte) (j + 3))
          assertEquals((byte) (j + 3), b3);
      }
    }
  }

  @Test
  public void hugeByteBufferLongMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    ByteBuffer[] buffers = new ByteBuffer[16];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = ByteBuffer.allocateDirect((int) (length / 16)).order(ByteOrder.nativeOrder());
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      lbWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      lbRead(buffers);
      long end = System.nanoTime();
      System.out.printf("ByteBuffer long took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void lbWrite(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 8) {
        buffer.putLong(j);
      }
    }
  }

  private static void lbRead(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      buffer.flip();
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 8) {
        long l2 = buffer.getLong();
        if (j != l2)
          assertEquals(j, l2);
      }
    }
  }

  @Test
  public void hugeByteBufferLongUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    ByteBuffer[] buffers = new ByteBuffer[16];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = ByteBuffer.allocateDirect((int) (length / 16)).order(ByteOrder.nativeOrder());
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      lbuWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      lbuRead(buffers);
      long end = System.nanoTime();
      System.out.printf("ByteBuffer long unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void lbuWrite(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 32) {
        buffer.putLong(j, j);
        buffer.putLong(j + 8, j + 8);
        buffer.putLong(j + 16, j + 16);
        buffer.putLong(j + 24, j + 24);
      }
    }
  }

  private static void lbuRead(ByteBuffer[] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      ByteBuffer buffer = buffers[i];
      int capacity = buffers[i].capacity();
      for (int j = 0; j < capacity; j += 32) {
        long l0 = buffer.getLong(j);
        long l1 = buffer.getLong(j + 8);
        long l2 = buffer.getLong(j + 16);
        long l3 = buffer.getLong(j + 24);
        if (j != l0)
          assertEquals(j, l0);
        if (j + 8 != l1)
          assertEquals(j + 8, l1);
        if (j + 16 != l2)
          assertEquals(j + 16, l2);
        if (j + 24 != l3)
          assertEquals(j + 24, l3);
      }
    }
  }

  @Test
  public void hugeByteArrayMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    byte[][] buffers = new byte[16][];
    for (int i = 0; i < buffers.length; i++) {
      System.out.println(i + ": " + Runtime.getRuntime().totalMemory() + " free " + Runtime.getRuntime().freeMemory());
      buffers[i] = new byte[(int) (length / 16)];
    }
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      baWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      baRead(buffers);
      long end = System.nanoTime();
      System.out.printf("byte[] took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void baWrite(byte[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      byte[] bytes = buffers[i];
      int capacity = bytes.length;
      for (int j = 0; j < capacity; j++) {
        bytes[j] = (byte) j;
      }
    }
  }

  private static void baRead(byte[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      byte[] bytes = buffers[i];
      int capacity = bytes.length;
      for (int j = 0; j < capacity; j++) {
        byte b = bytes[j];
        if (b != (byte) j)
          assertEquals((byte) j, b);
      }
    }
  }

  @Test
  public void hugeByteArrayUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    byte[][] buffers = new byte[16][];
    for (int i = 0; i < buffers.length; i++) {
      buffers[i] = new byte[(int) (length / 16)];
    }
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      bauWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      bauRead(buffers);
      long end = System.nanoTime();
      System.out.printf("byte[] unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void bauRead(byte[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      byte[] bytes = buffers[i];
      int capacity = bytes.length;
      for (int j = 0; j < capacity; j += 4) {
        byte b = bytes[j];
        byte b1 = bytes[j + 1];
        byte b2 = bytes[j + 2];
        byte b3 = bytes[j + 3];
        if (b != (byte) j)
          assertEquals((byte) j, b);
        if (b1 != (byte) (j + 1))
          assertEquals((byte) (j + 1), b1);
        if (b2 != (byte) (j + 2))
          assertEquals((byte) (j + 2), b2);
        if (b3 != (byte) (j + 3))
          assertEquals((byte) (j + 3), b3);
      }
    }
  }

  private static void bauWrite(byte[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      byte[] bytes = buffers[i];
      int capacity = bytes.length;
      for (int j = 0; j < capacity; j += 4) {
        bytes[j] = (byte) j;
        bytes[j + 1] = (byte) (j + 1);
        bytes[j + 2] = (byte) (j + 2);
        bytes[j + 3] = (byte) (j + 3);
      }
    }
  }

  @Test
  public void hugeByteArrayLongMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long[][] buffers = new long[16][];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = new long[(int) (length / 16 / 8)];
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      laWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      laRead(buffers);
      long end = System.nanoTime();
      System.out.printf("long[] took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void laWrite(long[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      long[] longs = buffers[i];
      int capacity = longs.length;
      for (int j = 0; j < capacity; j++)
        longs[j] = j;
    }
  }

  private static void laRead(long[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      long[] longs = buffers[i];
      int capacity = buffers[i].length;
      for (int j = 0; j < capacity; j++) {
        long b = longs[j];
        if (j != b)
          assertEquals(j, b);
      }
    }
  }

  @Test
  public void hugeByteArrayLongUnrolledMemoryPerformance() {
    Thread t = monitorThread();

    final long length = MEMORY_TESTED;
    System.out.println("allocate");
    long start2 = System.nanoTime();
    long[][] buffers = new long[16][];
    for (int i = 0; i < buffers.length; i++)
      buffers[i] = new long[(int) (length / 16 / 8)];
    long time2 = System.nanoTime() - start2;

    try {
      // add a GC to see what the GC times are like.
      System.gc();

      long start = System.nanoTime();
      System.out.println("writing");
      lauWrite(buffers);

      long mid = System.nanoTime();
      System.out.println("reading");
      lauRead(buffers);
      long end = System.nanoTime();
      System.out.printf("long[] unrolled took %,d us to allocate, %,d us to write and %,d us to read%n",
                           time2 / 1000, (mid - start) / 1000, (end - mid) / 1000);
    } finally {
      t.interrupt();
      System.gc();
    }
  }

  private static void lauWrite(long[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      long[] longs = buffers[i];
      int capacity = longs.length;
      for (int j = 0; j < capacity; j += 4) {
        longs[j] = j;
        longs[j + 1] = j + 1;
        longs[j + 2] = j + 2;
        longs[j + 3] = j + 3;
      }
    }
  }

  private static void lauRead(long[][] buffers) {
    for (int i = 0; i < buffers.length; i++) {
      long[] longs = buffers[i];
      int capacity = buffers[i].length;
      for (int j = 0; j < capacity; j += 4) {
        long l0 = longs[j];
        long l1 = longs[j + 1];
        long l2 = longs[j + 2];
        long l3 = longs[j + 3];
        if (j != l0)
          assertEquals(j, l0);
        if (j + 1 != l1)
          assertEquals(j + 1, l1);
        if (j + 2 != l2)
          assertEquals(j + 2, l2);
        if (j + 3 != l3)
          assertEquals(j + 3, l3);
      }
    }
  }

  public static final Unsafe UNSAFE; static {
    try {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      UNSAFE = (Unsafe) theUnsafe.get(null);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
