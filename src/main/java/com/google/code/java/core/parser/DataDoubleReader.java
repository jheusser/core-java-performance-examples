package com.google.code.java.core.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataDoubleReader implements DoubleReader {
    private final DataInputStream in;

    public DataDoubleReader(InputStream in) {
        this.in = new DataInputStream(new BufferedInputStream(in));
    }

    public double read() throws IOException {
        return in.readDouble();
    }

    public void close() {
        ParserUtils.close(in);
    }
}
