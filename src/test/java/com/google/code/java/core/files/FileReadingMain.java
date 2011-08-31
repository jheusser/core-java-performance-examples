/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileReadingMain {
  public static void main(String... args) throws IOException {
    File temp = File.createTempFile("deleteme", "zeros");
    FileOutputStream fos = new FileOutputStream(temp);
    fos.write(new byte[50 * 1024 * 1024]);
    fos.close();

    for (int i = 0; i < 3; i++)
      for (int blockSize = 1024 * 1024; blockSize >= 512; blockSize /= 2) {
        readFileNIO(temp, blockSize);
        readFile(temp, blockSize);
      }
  }

  private static void readFile(File temp, int blockSize) throws IOException {
    long start = System.nanoTime();
    byte[] bytes = new byte[blockSize];
    int r;
    for (r = 0; System.nanoTime() - start < 2e9; r++) {
      FileInputStream fis = new FileInputStream(temp);
      while (fis.read(bytes) > 0) ;
      fis.close();
    }
    long time = System.nanoTime() - start;
    System.out.printf("IO: Reading took %.3f ms using %,d byte blocks%n", time / r / 1e6, blockSize);
  }

  private static void readFileNIO(File temp, int blockSize) throws IOException {
    long start = System.nanoTime();
    ByteBuffer bytes = ByteBuffer.allocateDirect(blockSize);
    int r;
    for (r = 0; System.nanoTime() - start < 2e9; r++) {
      FileChannel fc = new FileInputStream(temp).getChannel();
      while (fc.read(bytes) > 0) {
        bytes.clear();
      }
      fc.close();
    }
    long time = System.nanoTime() - start;
    System.out.printf("NIO: Reading took %.3f ms using %,d byte blocks%n", time / r / 1e6, blockSize);
  }
}
