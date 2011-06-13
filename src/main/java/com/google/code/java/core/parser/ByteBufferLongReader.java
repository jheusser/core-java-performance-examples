package com.google.code.java.core.parser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferLongReader implements LongReader {
    private final ByteBuffer buffer;

    public ByteBufferLongReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public long read() throws IOException {
        return buffer.getLong();
    }

    @Override
    public void close() {
    }
}
