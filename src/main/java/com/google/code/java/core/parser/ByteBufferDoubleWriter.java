package com.google.code.java.core.parser;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferDoubleWriter implements DoubleWriter {
    protected final ByteBuffer buffer;

    public ByteBufferDoubleWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(double num) throws BufferOverflowException {
        buffer.putDouble(num);
    }

    @Override
    public void close() {

    }
}
