package com.google.code.java.core.parser;


import java.io.Closeable;
import java.io.IOException;

public interface LongWriter extends Closeable {
    void write(long num) throws IOException;

    @Override
    void close();
}
