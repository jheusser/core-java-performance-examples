/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ParserUtilsTest {
  @Test
  public void testDigits() {
    long l = (long) 1e18;
    for (int i = 19; i > 0; i--) {
      assertEquals(i, ParserUtils.digits(l));
      assertEquals(i - 1, ParserUtils.digits(l - 1));
      l /= 10;
    }
  }
}
