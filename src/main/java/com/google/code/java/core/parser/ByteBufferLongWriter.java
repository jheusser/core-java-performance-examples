package com.google.code.java.core.parser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferLongWriter implements LongWriter {
    private final ByteBuffer buffer;

    public ByteBufferLongWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(long num) throws IOException {
        buffer.putLong(num);
    }

    @Override
    public void close() {

    }
}
