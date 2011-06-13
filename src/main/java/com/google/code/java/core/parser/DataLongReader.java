package com.google.code.java.core.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataLongReader implements LongReader {
    private final DataInputStream in;

    public DataLongReader(InputStream in) {
        this.in = new DataInputStream(new BufferedInputStream(in));
    }

    public long read() throws IOException {
        return in.readLong();
    }

    public void close() {
        ParserUtils.close(in);
    }
}
