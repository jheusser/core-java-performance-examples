/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.sizeof;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Selector;

public class GarbageTest {
  @Test
  public void testFileExists() throws IOException {
    System.out.printf("The average size of calling File.exists/lastModified/length() is %.1f bytes%n", new SizeofUtil() {
      private File file = new File("/tmp/temporary.file");

      @Override
      protected int create() {
        file.exists();
        file.lastModified();
        file.length();
        return 1;
      }
    }.averageBytes());
  }

  @Test
  public void testSelectorSelectNow() throws IOException {
    final Selector selector = Selector.open();
    System.out.printf("The average size of calling Selector.selectNow() is %.1f bytes%n", new SizeofUtil() {
      @Override
      protected int create() {
        try {
          selector.selectNow();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        return 1;
      }
    }.averageBytes());
  }
}
