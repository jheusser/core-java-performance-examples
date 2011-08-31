/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.primitives;

import java.util.Arrays;

/**
 * User: peter
 */
public class SimpleIntHashMap {
  public static final int NO_VALUE = Integer.MIN_VALUE;
  private int[] keys = new int[16]; {
    Arrays.fill(keys, NO_VALUE);
  }

  private int[] values = new int[16];
  private int lastKey = keys.length / 2 - 1;

  public int get(int key) {
    int hash = key & lastKey;
    if (keys[hash] == key)
      return values[hash];
    return NO_VALUE;
  }

  public void put(int key, int value) {
    int hash = key & lastKey;
    if (keys[hash] != NO_VALUE && keys[hash] != key) {
      resize();
      hash = key & lastKey;
      if (keys[hash] != NO_VALUE && keys[hash] != key)
        throw new UnsupportedOperationException("Unable to handle collision.");
    }
    keys[hash] = key;
    values[hash] = value;
  }

  private void resize() {
    int len2 = keys.length * 2;
    int[] keys2 = new int[len2];
    Arrays.fill(keys2, NO_VALUE);
    int[] values2 = new int[len2];
    lastKey = len2 - 1;
    for (int i = 0; i < keys.length; i++) {
      int key = keys[i];
      int value = values[i];
      if (key == NO_VALUE) continue;
      int hash = key & lastKey;
      if (keys2[hash] != NO_VALUE)
        throw new UnsupportedOperationException("Unable to handle collision.");
      keys2[hash] = key;
      values2[hash] = value;
    }
    keys = keys2;
    values = values2;
  }

  public void clear() {
    Arrays.fill(keys, NO_VALUE);
  }
}
