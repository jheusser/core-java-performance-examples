package com.google.code.java.core.io;

import java.nio.ByteBuffer;

/**
 * @author peter.lawrey
 */
public interface ByteBufferConsumer extends ByteBufferPartialConsumer {
    /**
     * @param bb ByteBuffer which must be consumed in full.
     */
    public void consume(ByteBuffer bb);
}
