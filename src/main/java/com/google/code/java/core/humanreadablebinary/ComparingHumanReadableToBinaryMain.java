/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.humanreadablebinary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ComparingHumanReadableToBinaryMain {
  public static void main(String... args) throws IOException {
    List<Long> longs = new ArrayList<Long>();
    for (long i = -1; i <= 10; i++)
      longs.add(i);
    String asText = longs.toString();
    byte[] bytes1 = asText.getBytes();
    System.out.println("As text:  " + bytes1.length + " bytes long, " + asText);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(longs);
    oos.close();
    byte[] bytes2 = baos.toByteArray();
    System.out.println("As binary: " + bytes2.length + " bytes long, "
                           + new String(bytes2, 0).replaceAll("[^\\p{Graph}]", "."));
  }
}
