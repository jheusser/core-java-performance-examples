/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.socket;

import com.google.code.java.core.service.concurrency.NamedThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoService {
    private final ExecutorService service = Executors.newCachedThreadPool(new NamedThreadFactory("echo", true));
    private final ServerSocketChannel ssc;

    public EchoService(int port) throws IOException, InterruptedException {
        ssc = ServerSocketChannel.open();
        ssc.socket().setReuseAddress(true);
        try {
            ssc.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            Thread.sleep(100);
            ssc.socket().bind(new InetSocketAddress(port));
        }
        service.execute(new Acceptor());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 12345;
        System.out.println("Listening on port " + port);
        EchoService es = new EchoService(port);
        System.in.read();
    }

    public void stop() {
        service.shutdown();
        try {
            ssc.close();
        } catch (IOException ignored) {
        }
    }

    class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    SocketChannel sc = ssc.accept();
                    service.execute(new EchoHandler(sc));
                }
            } catch (Exception e) {
                if (!service.isShutdown())
                    e.printStackTrace();
            } finally {
                try {
                    ssc.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    static class EchoHandler implements Runnable {
        private final SocketChannel sc;

        public EchoHandler(SocketChannel sc) {
            this.sc = sc;
        }

        @Override
        public void run() {
            try {
                ByteBuffer bb = ByteBuffer.allocateDirect(256 * 1024);
                while (sc.isOpen()) {
                    if (sc.read(bb) < 0)
                        break;
                    bb.flip();
                    sc.write(bb);
                    bb.flip();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    sc.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
