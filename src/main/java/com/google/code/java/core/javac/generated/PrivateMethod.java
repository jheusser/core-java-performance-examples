/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.javac.generated;


public class PrivateMethod {
  private void init() {
    throw new UnsupportedOperationException();
  }

  ;

  class Inner {
    Inner() {
      init();
    }
  }

  public static void main(String... args) {
    PrivateMethod pm = new PrivateMethod();
    Inner inner = pm.new Inner();
  }
}
