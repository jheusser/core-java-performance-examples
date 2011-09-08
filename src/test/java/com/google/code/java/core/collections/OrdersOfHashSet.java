/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.collections;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OrdersOfHashSet {
  @Test
  public void testSetOrder() {
    Set<String> order = new HashSet<String>();
    Collection<String> elements = Arrays.asList("zero, one, two, three, four, five, six, seven, eight, nine, ten".split(", "));

    try {
      for (int i = 1; i > 0; i *= 2) {
        Set<String> set = new HashSet<String>(i, 100);
        set.addAll(elements);
        String str = set.toString();
        if (order.add(str))
          System.out.println("HashSet(" + i + ") order was " + str);
      }
    } catch (OutOfMemoryError ignored) {
    }
  }
}
