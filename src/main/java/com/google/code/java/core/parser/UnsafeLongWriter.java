package com.google.code.java.core.parser;

import java.io.IOException;

public class UnsafeLongWriter implements LongWriter {
    private long address;

    public UnsafeLongWriter(long address) {
        this.address = address;
    }

    @Override
    public void write(long num) throws IOException {
        ParserUtils.UNSAFE.putLong(address, num);
        address += 8;
    }

    @Override
    public void close() {

    }
}
