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

public class ByteBufferTextLongReader implements LongReader {
  private final ByteBuffer buffer;

  public ByteBufferTextLongReader(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  @Override
  public long read() throws BufferUnderflowException {
    long num = 0;
    boolean negative = false;
    while (true) {
      byte b = buffer.get();
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
