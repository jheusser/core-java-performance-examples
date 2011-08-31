/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.parser;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferTextDoubleWriter implements DoubleWriter {
  private static final long MAX_VALUE_DIVIDE_5 = Long.MAX_VALUE / 5;
  protected static final char SEPARATOR = '\n';
  private static final long MAX_DECIMALS = 1L << 53;
  private static final byte[] Infinity = "Infinity".getBytes();
  private static final byte[] NaN = "NaN".getBytes();

  protected final ByteBuffer buffer;

  public ByteBufferTextDoubleWriter(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  @Override
  public void write(double d) throws BufferOverflowException {
    long val = Double.doubleToRawLongBits(d);
    int sign = (int) (val >>> 63);
    int exp = (int) ((val >>> 52) & 2047);
    long mantissa = val & ((1L << 52) - 1);
    if (sign != 0) {
      writeByte('-');
    }
    if (exp == 0 && mantissa == 0) {
      writeByte('0');
      writeByte(SEPARATOR);
      return;
    } else if (exp == 2047) {
      if (mantissa == 0) {
        buffer.put(Infinity);
        writeByte(SEPARATOR);
      } else {
        buffer.put(NaN);
        writeByte(SEPARATOR);
      }
      return;
    } else if (exp > 0) {
      mantissa += 1L << 52;
    }
    final int shift = (1023 + 52) - exp;
    if (shift > 0) {
      // integer and faction
      if (shift < 53) {
        long intValue = mantissa >> shift;
        writeLong(intValue);
        mantissa -= intValue << shift;
        if (mantissa > 0) {
          writeByte('.');
          mantissa <<= 1;
          mantissa++;
          int precision = shift + 1;
          long error = 1;

          long value = intValue;
          int decimalPlaces = 0;
          while (mantissa > error) {
            // times 5*2 = 10
            mantissa *= 5;
            error *= 5;
            precision--;
            long num = (mantissa >> precision);
            value = value * 10 + num;
            writeByte((char) ('0' + num));
            mantissa -= num << precision;

            final double parsedValue = ByteBufferTextDoubleReader.asDouble(value, 0, sign != 0, ++decimalPlaces);
            if (parsedValue == d)
              break;
          }
        }
        writeByte(SEPARATOR);
        return;
      } else {
        // faction.
        writeByte('0');
        writeByte('.');
        mantissa <<= 6;
        mantissa += (1 << 5);
        int precision = shift + 6;

        long error = (1 << 5);

        long value = 0;
        int decimalPlaces = 0;
        while (mantissa > error) {
          while (mantissa > MAX_VALUE_DIVIDE_5) {
            mantissa >>>= 1;
            error = (error + 1) >>> 1;
            precision--;
          }
          // times 5*2 = 10
          mantissa *= 5;
          error *= 5;
          precision--;
          if (precision >= 64) {
            decimalPlaces++;
            writeByte('0');
            continue;
          }
          long num = (mantissa >>> precision);
          value = value * 10 + num;
          final char c = (char) ('0' + num);
          assert !(c < '0' || c > '9');
          writeByte(c);
          mantissa -= num << precision;
          final double parsedValue = ByteBufferTextDoubleReader.asDouble(value, 0, sign != 0, ++decimalPlaces);
          if (parsedValue == d)
            break;
        }
        writeByte(SEPARATOR);
        return;
      }
    }
    // large number
    mantissa <<= 10;
    int precision = -10 - shift;
    int digits = 0;
    while ((precision > 53 || mantissa > Long.MAX_VALUE >> precision) && precision > 0) {
      digits++;
      precision--;
      long mod = mantissa % 5;
      mantissa /= 5;
      int modDiv = 1;
      while (mantissa < MAX_VALUE_DIVIDE_5 && precision > 1) {
        precision -= 1;
        mantissa <<= 1;
        modDiv <<= 1;
      }
      mantissa += modDiv * mod / 5;
    }
    long val2 = precision > 0 ? mantissa << precision : mantissa >>> -precision;

    writeLong(val2);
    for (int i = 0; i < digits; i++)
      writeByte('0');
    writeByte(SEPARATOR);
    return;
  }

  protected void writeLong(long val2) {
    int digits = ParserUtils.digits(val2);
    // starting from the end, write each digit
    for (int i = digits - 1; i >= 0; i--) {
      // write the lowest digit.
      buffer.put(buffer.position() + i, (byte) (val2 % 10 + '0'));
      // remove that digit.
      val2 /= 10;
    }
    assert val2 == 0;
    // move the position to after the digits.
    buffer.position(buffer.position() + digits);
  }

  protected void writeByte(long c) {
    buffer.put((byte) c);
  }

  @Override
  public void close() {

  }
}
