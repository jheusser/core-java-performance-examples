package com.google.code.java.core.parser;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferDoubleReader implements DoubleReader {
    private final ByteBuffer buffer;

    public ByteBufferDoubleReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public double read() throws BufferUnderflowException {
        return buffer.getDouble();
    }

    @Override
    public void close() {
    }
}
