package com.google.code.java.core.parser;

public class UnsafeDoubleWriter implements DoubleWriter {
    private long address;

    public UnsafeDoubleWriter(long address) {
        this.address = address;
    }

    @Override
    public void write(double num) {
        ParserUtils.UNSAFE.putDouble(address, num);
        address += 8;
    }

    @Override
    public void close() {

    }
}
