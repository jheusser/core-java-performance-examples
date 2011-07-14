package com.google.code.java.core.parser;

import java.nio.ByteBuffer;

public class ByteBufferTextLongWriter implements LongWriter {
    private static final byte[] MIN_VALUE_TEXT = Long.toString(Long.MIN_VALUE).getBytes();
    private final ByteBuffer buffer;

    public ByteBufferTextLongWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(long num) {
        if (num < 0) {
            if (num == Long.MIN_VALUE) {
                buffer.put(MIN_VALUE_TEXT);
                return;
            }
            writeByte('-');
            num = -num;
        }
        if (num == 0) {
            writeByte('0');
            writeByte('\n');
        } else {
            int digits = ParserUtils.digits(num);
            for (int i = digits - 1; i >= 0; i--) {
                buffer.put(buffer.position() + i, (byte) (num % 10 + '0'));
                num /= 10;
            }
            buffer.position(buffer.position() + digits);
            writeByte('\n');
        }
    }

    private void writeByte(int c) {
        buffer.put((byte) c);
    }

    @Override
    public void close() {

    }
}
