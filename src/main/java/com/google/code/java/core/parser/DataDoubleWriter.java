package com.google.code.java.core.parser;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataDoubleWriter implements DoubleWriter {
    private final DataOutputStream out;

    public DataDoubleWriter(OutputStream os) {
        this.out = new DataOutputStream(new BufferedOutputStream(os));
    }

    @Override
    public void write(double num) throws IOException {
        out.writeDouble(num);
    }

    @Override
    public void close() {
        ParserUtils.close(out);
    }
}
