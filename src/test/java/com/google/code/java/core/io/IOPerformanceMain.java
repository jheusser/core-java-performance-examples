package com.google.code.java.core.io;

import com.google.code.java.core.time.HiresTimer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author peter.lawrey
 */
public class IOPerformanceMain {
    public static void main(String... args) throws IOException {
        for (int i = 0; i < 10; i++)
            doTest(new PassTestPipe());
    }

    public static void doTest(TestPipe pipe) throws IOException {
        final int maxLatency = 1000 + 1;
        final int[] latencies = new int[maxLatency];
        final AtomicLong lastExpected = new AtomicLong();

        pipe.source(new ByteBufferPartialConsumer() {
            long expected = 0;

            public void consume(ByteBuffer bb) {
//                long time = HiresTimer.nanoTime();
                while (bb.remaining() >= 32) {
                    long id = bb.getLong();
                    if (id != expected)
                        throw new AssertionError();
                    expected++;
                    long sentTime = bb.getLong();
                    for (int p = 0; p < 4; p++)
                        bb.getLong();
/*
                    int latency = (int) (time - sentTime);
                    if (latency>=maxLatency)
                        latency = maxLatency-1;
                    latencies[latency]++;
*/
                }
//                lastExpected.lazySet(expected);
            }
        });

        long start = HiresTimer.nanoTime();
        long end = start + 1000L * 1000 * 1000;
        ByteBufferPublisher sink = pipe.sink();
        long id = 0;
        while (true) {
            long now = HiresTimer.nanoTime();
            for (int i = 0; i < 100; i++) {
                ByteBuffer out = sink.acquireByteBuffer(64);
//            for (int j = 0; j < 2; j++) {
                out.putLong(id++);
                out.putLong(now);
                for (int p = 0; p < 4; p++)
                    out.putLong(now);
//            }
                sink.release(out);
            }
//            out.clear();
            if (now > end)
                break;
        }

//        while (lastExpected.get() < id)
//            Thread.yield();

        long time = HiresTimer.nanoTime() - start;

        System.out.printf("%s: Throughput %.1f M msg/s latency %,d / %,d / %,d / %,d : avg / 50 / 99 / 99.99 %% %n",
                pipe.toString(), id * 1e3 / time,
                time / id,
                fromEnd(latencies, id / 2),
                fromEnd(latencies, id / 100),
                fromEnd(latencies, id / 10000)
        );
    }

    private static int fromEnd(int[] latencies, long l) {
        for (int i = latencies.length - 1; i > 0; i--) {
            l -= latencies[i];
            if (l <= 0)
                return i;
        }
        return 0;
    }

    interface TestPipe {
        public ByteBufferPublisher sink();

        public void source(ByteBufferPartialConsumer source);
    }

    private static class PassTestPipe implements TestPipe {
        public ByteBufferPartialConsumer source = null;

        public ByteBufferPublisher sink() {
            return new ByteBufferPublisher() {
                final ByteBuffer bb = ByteBuffer.allocateDirect(4 * 1024).order(ByteOrder.nativeOrder());

                public ByteBuffer acquireByteBuffer(int capacity) throws IOException {
                    return bb;
                }

                public void release(ByteBuffer bb) throws IOException {
                    bb.flip();
                    source.consume(bb);
                    if (bb.remaining() == 0)
                        bb.clear();
                    else
                        bb.compact();
                }
            };
        }

        public void source(ByteBufferPartialConsumer source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }
}
