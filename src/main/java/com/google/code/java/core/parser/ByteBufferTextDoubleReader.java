/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferTextDoubleReader implements DoubleReader {
  public static final long MAX_VALUE_DIVIDE_10 = Long.MAX_VALUE / 10;
  private final ByteBuffer buffer;

  public ByteBufferTextDoubleReader(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  static double asDouble(long value, int exp, boolean negative, int decimalPlaces) {
    if (decimalPlaces > 0 && value < Long.MAX_VALUE / 2) {
      if (value < Long.MAX_VALUE / (1L << 32)) {
        exp -= 32;
        value <<= 32;
      }
      if (value < Long.MAX_VALUE / (1L << 16)) {
        exp -= 16;
        value <<= 16;
      }
      if (value < Long.MAX_VALUE / (1L << 8)) {
        exp -= 8;
        value <<= 8;
      }
      if (value < Long.MAX_VALUE / (1L << 4)) {
        exp -= 4;
        value <<= 4;
      }
      if (value < Long.MAX_VALUE / (1L << 2)) {
        exp -= 2;
        value <<= 2;
      }
      if (value < Long.MAX_VALUE / (1L << 1)) {
        exp -= 1;
        value <<= 1;
      }
    }
    for (; decimalPlaces > 0; decimalPlaces--) {
      exp--;
      long mod = value % 5;
      value /= 5;
      int modDiv = 1;
      if (value < Long.MAX_VALUE / (1L << 4)) {
        exp -= 4;
        value <<= 4;
        modDiv <<= 4;
      }
      if (value < Long.MAX_VALUE / (1L << 2)) {
        exp -= 2;
        value <<= 2;
        modDiv <<= 2;
      }
      if (value < Long.MAX_VALUE / (1L << 1)) {
        exp -= 1;
        value <<= 1;
        modDiv <<= 1;
      }
      value += modDiv * mod / 5;
    }
    final double d = Math.scalb((double) value, exp);
    return negative ? -d : d;
  }

  @Override
  public double read() throws BufferUnderflowException {
    long value = 0;
    int exp = 0;
    boolean negative = false;
    int decimalPlaces = Integer.MIN_VALUE;
    while (true) {
      byte ch = buffer.get();
      if (ch >= '0' && ch <= '9') {
        while (value >= MAX_VALUE_DIVIDE_10) {
          value >>>= 1;
          exp++;
        }
        value = value * 10 + (ch - '0');
        decimalPlaces++;
      } else if (ch == '-') {
        negative = true;
      } else if (ch == '.') {
        decimalPlaces = 0;
      } else {
        break;
      }
    }

    return asDouble(value, exp, negative, decimalPlaces);
  }

  @Override
  public void close() {
  }
}
