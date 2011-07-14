package com.google.code.java.core.parser;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferLongWriter implements LongWriter {
    private final ByteBuffer buffer;

    public ByteBufferLongWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(long num) throws BufferOverflowException {
        buffer.putLong(num);
    }

    @Override
    public void close() {

    }
}
