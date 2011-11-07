package com.google.code.java.core.io;

/**
 * @author peter.lawrey
 */
public interface ByteBufferProcessor extends ByteBufferPartialConsumer {
    /**
     * @param out The ByteBufferPublisher for this Processor.
     */
    public void out(ByteBufferPublisher out);
}
