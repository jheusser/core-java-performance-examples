/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class DecimalFormatDoubleReader implements DoubleReader {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
  private final BufferedReader br;

  public DecimalFormatDoubleReader(BufferedReader br) {
    this.br = br;
  }

  @Override
  public double read() throws IOException {
    String num = null;
    try {
      num = br.readLine();
      return DECIMAL_FORMAT.parse(num).doubleValue();
    } catch (ParseException e) {
      throw new IOException("Unable to parse '" + num + '\'', e);
    }
  }

  @Override
  public void close() {
    ParserUtils.close(br);
  }
}
