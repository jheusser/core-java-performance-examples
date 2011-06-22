package com.google.code.java.core.parser;

import java.io.IOException;
import java.io.PrintWriter;

public class PrintLongWriter implements LongWriter {
    private final PrintWriter pw;

    public PrintLongWriter(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void write(long num) throws IOException {
        pw.println(num);
    }

    @Override
    public void close() {
        pw.close();
    }
}
