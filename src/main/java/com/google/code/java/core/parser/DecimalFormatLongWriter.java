/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class DecimalFormatLongWriter implements LongWriter {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");
  private final PrintWriter pw;

  public DecimalFormatLongWriter(PrintWriter pw) {
    this.pw = pw;
  }

  @Override
  public void write(long num) {
    pw.println(DECIMAL_FORMAT.format(num));
  }

  @Override
  public void close() {
    pw.close();
  }
}
