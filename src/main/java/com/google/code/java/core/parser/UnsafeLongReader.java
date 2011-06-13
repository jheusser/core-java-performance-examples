package com.google.code.java.core.parser;

import java.io.IOException;

public class UnsafeLongReader implements LongReader {
    private long address;

    public UnsafeLongReader(long address) {
        this.address = address;
    }

    @Override
    public long read() throws IOException {
        long num = ParserUtils.UNSAFE.getLong(address);
        address += 8;
        return num;
    }

    @Override
    public void close() {
    }
}
