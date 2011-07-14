package com.google.code.java.core.parser;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferLongReader implements LongReader {
    private final ByteBuffer buffer;

    public ByteBufferLongReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public long read() throws BufferUnderflowException {
        return buffer.getLong();
    }

    @Override
    public void close() {
    }
}
