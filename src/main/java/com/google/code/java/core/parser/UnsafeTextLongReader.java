/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

public class UnsafeTextLongReader implements LongReader {
  private long address;

  public UnsafeTextLongReader(long address) {
    this.address = address;
  }

  @Override
  public long read() {
    long num = 0;
    boolean negative = false;
    while (true) {
      byte b = ParserUtils.UNSAFE.getByte(address++);
//            if (b >= '0' && b <= '9')
      if ((b - ('0' + Integer.MIN_VALUE)) <= 9 + Integer.MIN_VALUE)
        num = num * 10 + b - '0';
      else if (b == '-')
        negative = true;
      else
        break;
    }
    return negative ? -num : num;
  }

  @Override
  public void close() {
  }
}
