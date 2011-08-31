/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import sun.misc.Unsafe;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public enum ParserUtils {
  ;
  static final long[] TENS = new long[19];

  static {
    TENS[0] = 1;
    for (int i = 1; i < TENS.length; i++)
      TENS[i] = TENS[i - 1] * 10;
  }

  public static int digits(long l) {
    int idx = Arrays.binarySearch(TENS, l);
    return idx >= 0 ? idx + 1 : ~idx;
  }

  public static void close(Closeable closeable) {
    if (closeable != null) try {
      closeable.close();
    } catch (IOException ignored) {
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
