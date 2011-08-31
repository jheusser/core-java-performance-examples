/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

public class UnsafeDoubleReader implements DoubleReader {
  private long address;

  public UnsafeDoubleReader(long address) {
    this.address = address;
  }

  @Override
  public double read() {
    double num = ParserUtils.UNSAFE.getDouble(address);
    address += 8;
    return num;
  }

  @Override
  public void close() {
  }
}
