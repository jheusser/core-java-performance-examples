/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.service.api;

import java.nio.ByteBuffer;

public interface ByteBufferConsumer {
    public ByteBuffer acquireByteBuffer(int capacity);

    public void releaseByteBuffer(ByteBuffer bb);
}
