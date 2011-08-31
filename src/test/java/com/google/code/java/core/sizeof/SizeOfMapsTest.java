/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.sizeof;

import gnu.trove.TIntLongHashMap;
import gnu.trove.decorator.TIntLongHashMapDecorator;
import javolution.util.FastMap;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class SizeOfMapsTest {
  @Test
  public void sizeOfMaps() {
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new TIntLongHashMapDecorator(new TIntLongHashMap(capacity));
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return FastMap.newInstance();
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new IdentityHashMap<Integer, Long>(capacity * 4 / 3);
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new ConcurrentSkipListMap<Integer, Long>();
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new TreeMap<Integer, Long>();
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new HashMap<Integer, Long>(capacity * 4 / 3);
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return Collections.synchronizedMap(new HashMap<Integer, Long>(capacity * 4 / 3));
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new ConcurrentHashMap<Integer, Long>(capacity * 4 / 3);
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return (Map) new Properties();
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new Hashtable<Integer, Long>(capacity);
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new LinkedHashMap<Integer, Long>(capacity * 4 / 3);
      }
    });
    sizePerEntry(new Builder<Map<Integer, Long>>() {
      @Override
      public Map<Integer, Long> build(int capacity) {
        return new WeakHashMap<Integer, Long>(capacity * 4 / 3);
      }
    });
  }

  interface Builder<T> {
    T build(int capacity);
  }

  private static void sizePerEntry(final Builder<Map<Integer, Long>> mapBuilder) {
    Object build = mapBuilder.build(16);
    if (build instanceof TIntLongHashMapDecorator)
      build = ((TIntLongHashMapDecorator) build).getMap();
    System.out.printf("The average size of a %s per entry with 16/1024 entries is %.1f/%.1f bytes%n",
                         build.getClass().getSimpleName(),
                         new SizeofUtil() {
                           Map<Integer, Long> map;

                           @Override
                           protected int create() {
                             map = mapBuilder.build(16);
                             populate(map, 16);
                             return 16;
                           }
                         }.averageBytes(),
                         new SizeofUtil() {
                           Map<Integer, Long> map;

                           @Override
                           protected int create() {
                             map = mapBuilder.build(1024);
                             populate(map, 1024);
                             return 1024;
                           }
                         }.averageBytes());
  }

  static void populate(Map<Integer, Long> map, long entries) {
    if (map instanceof TIntLongHashMapDecorator) {
      TIntLongHashMap _map = ((TIntLongHashMapDecorator) map).getMap();
      for (int i = 0; i < entries; i++)
        _map.put(i * 65, i * 65);
    } else {
      for (int i = 0; i < entries; i++)
        map.put(i * 65, i * 65L);
    }
    if (map instanceof FastMap)
      FastMap.recycle((FastMap) map);
  }
}
