package com.google.code.java.core.parser;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class InMemoryPerfTest {
    interface PerfTest {
        LongReader longReader();

        LongWriter longWriter();

        void finish();

        String toString();
    }

    public void doPerf(PerfTest perfTest) throws IOException, InterruptedException {
        int runs = 1001;
        long[] times = new long[runs];
        long len = 128 * 1000;
        for (int n = 0; n < runs; n++) {
            Thread.sleep(1);
            long start = System.nanoTime();

            LongWriter lw = perfTest.longWriter();
            lw.write(len);
            for (long i = 0; i < len; i++) {
                lw.write(i);
            }
            lw.close();

            LongReader lr = perfTest.longReader();
            long len2 = lr.read();
            assertEquals(len, len2);

            for (long i = 0; i < len; i++) {
                lr.read();
            }
            lr.close();
            times[n] = System.nanoTime() - start;
        }
        perfTest.finish();
        Arrays.sort(times);
        System.out.printf(perfTest + ": Typically took %.1f ns to write/read per long.%n", (double) times[runs / 2] / len, len / 1000);
    }

    @Test
    public void testUnsafePerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            final long address = ParserUtils.UNSAFE.allocateMemory(1024 * 1024);

            @Override
            public LongWriter longWriter() {
                return new UnsafeLongWriter(address);
            }

            @Override
            public LongReader longReader() {
                return new UnsafeLongReader(address);
            }

            @Override
            public String toString() {
                return "Unsafe binary";
            }

            @Override
            public void finish() {
                ParserUtils.UNSAFE.freeMemory(address);
            }
        });
    }

    @Test
    public void testByteBufferPerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);

            @Override
            public LongWriter longWriter() {
                buffer.clear();
                return new ByteBufferLongWriter(buffer);
            }

            @Override
            public LongReader longReader() {
                buffer.flip();
                return new ByteBufferLongReader(buffer);
            }

            @Override
            public String toString() {
                return "ByteBuffer binary";
            }

            @Override
            public void finish() {
            }
        });
    }

    @Test
    public void testUnsafeTextPerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            final long address = ParserUtils.UNSAFE.allocateMemory(1024 * 1024);

            @Override
            public LongWriter longWriter() {
                return new UnsafeTextLongWriter(address);
            }

            @Override
            public LongReader longReader() {
                return new UnsafeTextLongReader(address);
            }

            @Override
            public String toString() {
                return "Unsafe text";
            }

            @Override
            public void finish() {
                ParserUtils.UNSAFE.freeMemory(address);
            }
        });
    }

    @Test
    public void testDataPerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            ByteArrayOutputStream baos;

            @Override
            public LongWriter longWriter() {
                baos = new ByteArrayOutputStream();
                return new DataLongWriter(baos);
            }

            @Override
            public LongReader longReader() {
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                return new DataLongReader(bais);
            }

            @Override
            public String toString() {
                return "DataStream binary";
            }

            @Override
            public void finish() {
            }
        });
    }

    @Test
    public void testPrintPerf() throws IOException, InterruptedException {
        doPerf(new PerfTest() {
            ByteArrayOutputStream baos;

            @Override
            public LongWriter longWriter() {
                baos = new ByteArrayOutputStream();
                return new PrintLongWriter(new PrintWriter(new OutputStreamWriter(baos)));
            }

            @Override
            public LongReader longReader() {
                return new PrintLongReader(
                        new BufferedReader(
                                new InputStreamReader(
                                        new ByteArrayInputStream(baos.toByteArray()))));
            }

            @Override
            public String toString() {
                return "Print text";
            }

            @Override
            public void finish() {
            }
        });
    }
}
