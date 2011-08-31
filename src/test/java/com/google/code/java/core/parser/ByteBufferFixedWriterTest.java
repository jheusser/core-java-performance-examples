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

public class ByteBufferFixedWriterTest {
  @Test
  public void testWrite() {
    doTest(0.9375);
    doTest(75116.075116);
/*
        doTest(0.9);
        doTest(0.1);
*/

    doTest(0.0);
    doTest(1.0);
//        doTest(Double.POSITIVE_INFINITY);
//        doTest(Double.NaN);
    for (double d = Long.MAX_VALUE * 2.0; d > 1; d -= d / 16)
      doTest(d);

    for (long d = (long) 1e9; d > 1; d -= d / 16 + 1)
      doTest(d / 1e9);

    // powers of 10
    for (double t = 10; t < 1e9; t *= 10) {
      doTest(1 / t);
      doTest(1 - 1 / t);
    }
  }

  private void doTest(double v) {
    ByteBuffer buffer = ByteBuffer.allocate(128);
    ByteBufferFixedWriter writer = new ByteBufferFixedWriter(buffer, 9);
    writer.write(v);
    writer.close();
    final String text = new String(buffer.array(), 0, buffer.position());
    System.out.print(text);

    BufferedReader br = new BufferedReader(new StringReader(text));
    PrintDoubleReader reader = new PrintDoubleReader(br);
    try {
      double v2 = reader.read();
      if (v2 == v) return;

      ByteBuffer buffer2 = ByteBuffer.allocate(128);
      ByteBufferFixedWriter writer2 = new ByteBufferFixedWriter(buffer2, 9);
      writer2.write(v);
      writer2.close();

      assertEquals(v, v2);
    } catch (NumberFormatException nfe) {
      ByteBuffer buffer2 = ByteBuffer.allocate(128);
      ByteBufferFixedWriter writer2 = new ByteBufferFixedWriter(buffer2, 9);
      writer2.write(v);
      writer2.close();

      final AssertionError ae = new AssertionError("Unable to read number for " + v);
      ae.initCause(nfe);
      throw ae;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
}
