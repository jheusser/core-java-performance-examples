package com.google.code.java.core.parser;


import java.io.Closeable;
import java.io.IOException;

public interface DoubleWriter extends Closeable {
    void write(double num) throws IOException;

    @Override
    void close();
}
