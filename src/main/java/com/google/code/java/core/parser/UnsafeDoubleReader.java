package com.google.code.java.core.parser;

public class UnsafeDoubleReader implements DoubleReader {
    private long address;

    public UnsafeDoubleReader(long address) {
        this.address = address;
    }

    @Override
    public double read() {
        double num = ParserUtils.UNSAFE.getDouble(address);
        address += 8;
        return num;
    }

    @Override
    public void close() {
    }
}
