/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferFixedWriter extends ByteBufferTextDoubleWriter {
  private final long factor;
  private long maxFactor;
  private long maxValue;

  public ByteBufferFixedWriter(ByteBuffer buffer, int precision) {
    super(buffer);
    factor = ParserUtils.TENS[precision];
    maxValue = 1L << 53 / factor;
  }

  @Override
  public void write(double num) throws BufferOverflowException {
    if (Math.abs(num) < maxValue)
      write0(num);
    else
      super.write(num);
  }

  private void write0(double num) {
    if (num < 0) {
      writeByte('-');
      num = -num;
    } else if (num == 0) {
      writeByte('0');
      writeByte(SEPARATOR);
      return;
    }
    maxFactor = Long.MAX_VALUE / factor;
    long factor = this.factor;
    if (num > maxFactor) {
      while (factor > 1 && num > Long.MAX_VALUE / factor)
        factor /= 10;
    }
    long value = (long) (num * factor + 0.5);
    while (factor > 1 && value % 10 == 0) {
      factor /= 10;
      value /= 10;
    }
    if (factor == 1) {
      writeLong(value);
      writeByte(SEPARATOR);
      return;
    }

    int digits = ParserUtils.digits(value);
    int factorDigits = ParserUtils.digits(factor);
    if (digits < factorDigits)
      digits = factorDigits;
    final int position = buffer.position();
    for (int i = digits; i >= 0; i--) {
      if (factorDigits == 1) {
        buffer.put(position + i, (byte) '.');
        factorDigits = 0;
      } else {
        buffer.put(position + i, (byte) ('0' + value % 10));
        value /= 10;
        factorDigits--;
      }
    }
    buffer.position(position + digits + 1);
    writeByte(SEPARATOR);
  }
}
