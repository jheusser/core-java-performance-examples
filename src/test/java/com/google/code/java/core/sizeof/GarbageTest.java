package com.google.code.java.core.sizeof;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Selector;

public class GarbageTest {
    @Test
    public void testFileExists() throws IOException {
        System.out.printf("The average size of calling File.exists/lastModified/length() is %.1f bytes%n", new SizeofUtil() {
            private File file = new File("/tmp/temporary.file");

            @Override
            protected int create() {
                file.exists();
                file.lastModified();
                file.length();
                return 1;
            }
        }.averageBytes());
    }

    @Test
    public void testSelectorSelectNow() throws IOException {
        final Selector selector = Selector.open();
        System.out.printf("The average size of calling Selector.selectNow() is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                try {
                    selector.selectNow();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                return 1;
            }
        }.averageBytes());
    }
}
