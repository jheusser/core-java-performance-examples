package com.google.code.java.core.io;

import java.nio.ByteBuffer;

/**
 * @author peter.lawrey
 */
public interface ByteBufferPartialConsumer {
    /**
     * @param bb ByteBuffer which may, or may not be consumed in full.
     */
    public void consume(ByteBuffer bb);
}
