/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataLongReader implements LongReader {
  private final DataInputStream in;

  public DataLongReader(InputStream in) {
    this.in = new DataInputStream(new BufferedInputStream(in));
  }

  public long read() throws IOException {
    return in.readLong();
  }

  public void close() {
    ParserUtils.close(in);
  }
}
