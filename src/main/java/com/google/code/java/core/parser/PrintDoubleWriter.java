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

public class PrintDoubleWriter implements DoubleWriter {
  private final PrintWriter pw;

  public PrintDoubleWriter(PrintWriter pw) {
    this.pw = pw;
  }

  @Override
  public void write(double num) {
    pw.println(num);
  }

  @Override
  public void close() {
    pw.close();
  }
}
