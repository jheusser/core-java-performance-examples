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

public class ByteBufferLongReader implements LongReader {
  private final ByteBuffer buffer;

  public ByteBufferLongReader(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  @Override
  public long read() throws BufferUnderflowException {
    return buffer.getLong();
  }

  @Override
  public void close() {
  }
}
