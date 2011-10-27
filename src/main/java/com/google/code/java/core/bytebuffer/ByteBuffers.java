package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author peter.lawrey
 */
public enum ByteBuffers {
    ;

    public static void read(ByteBuffer bb, AsciiText text) {
        text.readFrom(bb);
    }

    public static int readInt(ByteBuffer bb) {
        int n = bb.get();
        if (n == -128)
            return bb.getInt();
        return n;
    }

    public static long readLong(ByteBuffer bb) {
        int n = bb.get();
        if (n == -128)
            return bb.getLong();
        return n;
    }

    public static void write(ByteBuffer bb, Enum anEnum) {
        bb.put((byte) anEnum.ordinal());
    }

    public static int readOrdinal(ByteBuffer bb) {
        return bb.get() & 0xFF;
    }

    public static void write(ByteBuffer bb, int num) {
        if (num < -127 || num > 127) {
            bb.put((byte) -128);
            bb.putInt(num);
        } else {
            bb.put((byte) num);
        }
    }

    public static void write(ByteBuffer bb, long num) {
        if (num < -127 || num > 127) {
            bb.put((byte) -128);
            bb.putLong(num);
        } else {
            bb.put((byte) num);
        }
    }

    public static void write(ByteBuffer bb, AsciiText text) {
        text.writeTo(bb);
    }
}
