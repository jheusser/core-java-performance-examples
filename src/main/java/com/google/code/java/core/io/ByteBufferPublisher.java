package com.google.code.java.core.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author peter.lawrey
 */
public interface ByteBufferPublisher {
    public ByteBuffer acquireByteBuffer(int capacity) throws IOException;

    public void release(ByteBuffer bb) throws IOException;
}
