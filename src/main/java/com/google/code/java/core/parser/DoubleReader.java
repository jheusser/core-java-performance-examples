package com.google.code.java.core.parser;


import java.io.Closeable;
import java.io.IOException;

public interface DoubleReader extends Closeable {
    double read() throws IOException;

    @Override
    void close();
}
