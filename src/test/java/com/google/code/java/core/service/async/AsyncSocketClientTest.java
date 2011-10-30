/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.service.async;

import com.google.code.java.core.service.api.ByteBufferListener;
import com.google.code.java.core.socket.EchoService;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AsyncSocketClientTest {
    @Test
    public void testSend() throws IOException, InterruptedException {
        EchoService es = new EchoService(12345);

        ByteBufferListener listener = new ByteBufferListener() {
            @Override
            public void process(ByteBuffer bb) {

            }

            @Override
            public void processOther() {
            }
        };
        AsyncSocketClient asc = new AsyncSocketClient("localhost", 12345, 1024, listener);
//        asc.readingBuffer(12);
        es.stop();
    }
}
