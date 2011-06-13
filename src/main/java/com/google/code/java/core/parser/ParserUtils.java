package com.google.code.java.core.parser;

import sun.misc.Unsafe;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;

public enum ParserUtils {
    ;

    public static void close(Closeable closeable) {
        if (closeable != null) try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    public static final Unsafe UNSAFE; static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
