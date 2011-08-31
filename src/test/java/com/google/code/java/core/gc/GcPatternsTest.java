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

import java.util.LinkedList;
import java.util.List;

public class GcPatternsTest {
  // these test should be run with -verbosegc
  @Test
  public void createLotsOfSmallObjects() {
    long start = System.nanoTime();
    while (System.nanoTime() - start < 20 * 60e9) {
      for (int i = 0; i < 9000; i++)
        new Object().hashCode();
    }
  }

  @Test
  public void createLotsOfLargeObject() {
    long start = System.nanoTime();
    while (System.nanoTime() - start < 2e9) {
      for (int i = 0; i < 1000; i++)
        new byte[1024].hashCode();
    }
  }

  @Test
  public void createVariousLifeObjects() {
    List<byte[]> shortList = new LinkedList<byte[]>(),
        mediumList = new LinkedList<byte[]>(),
        longList = new LinkedList<byte[]>(),
        vlongList = new LinkedList<byte[]>();
    long start = System.nanoTime();
    while (System.nanoTime() - start < 60e9) {
      for (int i = 0; i < 1000; i++) {
        addToSize(shortList, 256 * 1024); // ~ 256 MB
        if (i % 10 == 0)
          addToSize(mediumList, 256 * 1024); // ~ 256 MB
        if (i % 100 == 0)
          addToSize(longList, 256 * 1024); // ~ 256 MB
        if (i % 1000 == 0)
          addToSize(vlongList, 256 * 1024); // ~ 256 MB
      }
    }
  }

  private static void addToSize(List<byte[]> list, int maxSize) {
    list.add(new byte[1000]); // ~1 KB with headers etc
    if (list.size() > maxSize)
      list.remove(0);
  }
}
