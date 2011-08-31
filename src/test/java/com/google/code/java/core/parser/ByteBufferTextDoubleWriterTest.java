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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;

import static junit.framework.Assert.assertEquals;

public class ByteBufferTextDoubleWriterTest {
  @Test
  public void testWrite() {
    doTest(75116.075116);
/*
        doTest(0.9);
        doTest(0.1);
*/

    doTest(0.0);
    doTest(1.0);
//        doTest(Double.POSITIVE_INFINITY);
//        doTest(Double.NaN);
    for (double d = Long.MAX_VALUE * 2.0; d > 1e-18; d -= d / 16)
      doTest(d);

    // powers of 10
    for (double t = 10; t < 1e16; t *= 10) {
      doTest(1 / t);
      doTest(1 - 1 / t);
    }
  }

  private void doTest(double v) {
    ByteBuffer buffer = ByteBuffer.allocate(128);
    ByteBufferTextDoubleWriter writer = new ByteBufferTextDoubleWriter(buffer);
    writer.write(v);
    writer.close();
    final String text = new String(buffer.array(), 0, buffer.position());
    System.out.print(text);

    BufferedReader br = new BufferedReader(new StringReader(text));
    PrintDoubleReader reader = new PrintDoubleReader(br);
    try {
      double v2 = reader.read();
      assertEquals(v, v2);
    } catch (NumberFormatException nfe) {
      ByteBuffer buffer2 = ByteBuffer.allocate(128);
      ByteBufferTextDoubleWriter writer2 = new ByteBufferTextDoubleWriter(buffer);
      writer2.write(v);
      writer2.close();

      final AssertionError ae = new AssertionError("Unable to read number for " + v);
      ae.initCause(nfe);
      throw ae;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    buffer.flip();
    ByteBufferTextDoubleReader reader2 = new ByteBufferTextDoubleReader(buffer);
    double v3 = reader2.read();
    if (v != v3) {
      buffer.position(0);
      ByteBufferTextDoubleReader reader3 = new ByteBufferTextDoubleReader(buffer);
      double v3b = reader3.read();
      assertEquals(v, v3);
    }
  }
}
