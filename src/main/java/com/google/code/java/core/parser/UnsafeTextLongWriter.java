/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

public class UnsafeTextLongWriter implements LongWriter {
  private static final byte[] MIN_VALUE_TEXT = Long.toString(Long.MIN_VALUE).getBytes();

  private long address;

  public UnsafeTextLongWriter(long address) {
    this.address = address;
  }

  @Override
  public void write(long num) {
    if (num < 0) {
      if (num == Long.MIN_VALUE) {
        for (int i = 0; i < MIN_VALUE_TEXT.length; i++) {
          ParserUtils.UNSAFE.putByte(address++, MIN_VALUE_TEXT[i]);
          return;
        }
      }
      writeByte('-');
      num = -num;
    }
    if (num == 0) {
      writeByte('0');
      writeByte('\n');
    } else if (num < 10000000) {
      int digits = num < 10000 ? num < 100 ? num < 10 ? 1 : 2 : num < 1000 ? 3 : 4 :
                       num < 1000000 ? num < 100000 ? 5 : 6 : num < 10000000 ? 7 : 8;
      // assume little endian.
      int shift = 8 * digits;
      long val = (long) '\n' << shift;
      shift -= 8;
      for (int i = 0; i < digits; i++, shift -= 8) {
        val |= (long) (num % 10 + '0') << shift;
        num /= 10;
      }
      ParserUtils.UNSAFE.putLong(address, val);
      address += digits + 1;
    } else {
      int digits = ParserUtils.digits(num);
      for (int i = digits - 1; i >= 0; i--) {
        ParserUtils.UNSAFE.putByte(address + i, (byte) (num % 10 + '0'));
        num /= 10;
      }
      address += digits;
      writeByte('\n');
    }
  }

  private void writeByte(int c) {
    ParserUtils.UNSAFE.putByte(address++, (byte) c);
  }

  @Override
  public void close() {

  }
}
