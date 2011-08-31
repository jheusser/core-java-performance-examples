/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.files;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;

public class OpenCloseFilesTest {
  public static final String TMPFS_DIR = System.getProperty("tmpfs.dir", "/tmp/");
  private static final String LOCAL_FS_DIR = System.getProperty("user.home");
  public static final String NFS_DIR = System.getProperty("nfs.dir");

  @Test
  public void tmpfsPerfTest() throws IOException {
    int files = 100 * 1000;

    // must be a tmpfs file system.
    File dir = new File(TMPFS_DIR + "/deleteme");
    assertTrue(dir.mkdir());
    System.out.println("Created " + dir.getAbsolutePath());

    byte[] bytes = new byte[256];
    long start = 0;
    for (int i = -files / 10; i < files; i++) {
      if (i == 0) start = System.nanoTime();
      File file = new File(dir, Integer.toString(i));
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(bytes);
      fos.close();
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %.1f us to write to TMPFS%n", time / 1e3 / files);

    long start2 = System.nanoTime();
    for (int i = -files / 10; i < files; i++) {
      File file = new File(dir, Integer.toString(i));
      file.delete();
    }
    assertTrue(dir.delete());
    long time2 = System.nanoTime() - start2;
    System.out.println("Removed " + dir.getAbsolutePath());

    System.out.printf("Took an average of %.1f us to delete files from TMPFS%n", time2 / 1e3 / (files + files / 10 + 1));

  }

  @Test
  public void localfsPerfTest() throws IOException {
    int files = 10 * 1000;

    // must be a tmpfs file system.
    File dir = new File(LOCAL_FS_DIR + "/deleteme");
    assertTrue(dir.mkdir());
    System.out.println("Created " + dir.getAbsolutePath());

    byte[] bytes = new byte[256];
    long start = System.nanoTime();
    for (int i = 0; i < files; i++) {
      File file = new File(dir, Integer.toString(i));
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(bytes);
      fos.close();
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %.1f us to write to LOCAL filesystem%n", time / 1e3 / files);

    long start2 = System.nanoTime();
    for (int i = 0; i < files; i++) {
      File file = new File(dir, Integer.toString(i));
      file.delete();
    }
    assertTrue(dir.delete());
    long time2 = System.nanoTime() - start2;
    System.out.println("Removed " + dir.getAbsolutePath());

    System.out.printf("Took an average of %.1f us to delete files from LOCAL filesystem%n", time2 / 1e3 / (files + 1));
  }

  @Test
  public void nfsPerfTest() throws IOException {
    int files = 250;
    if (NFS_DIR == null) {
      System.err.println("You must set -Dnfs.dir= to the location of a writable NFS drive to run this test");
      return;
    }

    // must be a tmpfs file system.
    File dir = new File(NFS_DIR + "/deleteme");
    assertTrue(dir.mkdir());
    System.out.println("Created " + dir.getAbsolutePath());

    byte[] bytes = new byte[256];
    long start = 0;
    for (int i = 0; i < files; i++) {
      if (i == 0) start = System.nanoTime();
      File file = new File(dir, Integer.toString(i));
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(bytes);
      fos.close();
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %,d us to write to the NFS drive%n", time / 1000 / files);

    long start2 = System.nanoTime();
    for (int i = 0; i < files; i++) {
      File file = new File(dir, Integer.toString(i));
      file.delete();
    }
    assertTrue(dir.delete());
    long time2 = System.nanoTime() - start2;
    System.out.println("Removed " + dir.getAbsolutePath());

    System.out.printf("Took an average of %,d us to delete files from the NFS drive%n", time2 / 1000 / (files + 1));
  }
}
