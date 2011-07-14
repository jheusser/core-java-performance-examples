package com.google.code.java.core.parser;

public class UnsafeLongReader implements LongReader {
    private long address;

    public UnsafeLongReader(long address) {
        this.address = address;
    }

    @Override
    public long read() {
        long num = ParserUtils.UNSAFE.getLong(address);
        address += 8;
        return num;
    }

    @Override
    public void close() {
    }
}
