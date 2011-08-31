/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.javac.generated;

public class PrivateField {
  private int num = 0;

  public class Inner {
    public void set(int n) {
      num = n;
    }

    public int get() {
      return num;
    }

    public void increment() {
      num++;
    }

    public void multiply(int n) {
      num *= n;
    }
  }
}
