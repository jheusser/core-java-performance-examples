/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataDoubleWriter implements DoubleWriter {
  private final DataOutputStream out;

  public DataDoubleWriter(OutputStream os) {
    this.out = new DataOutputStream(new BufferedOutputStream(os));
  }

  @Override
  public void write(double num) throws IOException {
    out.writeDouble(num);
  }

  @Override
  public void close() {
    ParserUtils.close(out);
  }
}
