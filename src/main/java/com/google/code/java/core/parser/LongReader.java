package com.google.code.java.core.parser;


import java.io.Closeable;
import java.io.IOException;

public interface LongReader extends Closeable {
    long read() throws IOException;

    @Override
    void close();
}
