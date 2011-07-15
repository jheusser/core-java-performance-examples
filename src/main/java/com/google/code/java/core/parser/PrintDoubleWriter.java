package com.google.code.java.core.parser;

import java.io.PrintWriter;

public class PrintDoubleWriter implements DoubleWriter {
    private final PrintWriter pw;

    public PrintDoubleWriter(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void write(double num) {
        pw.println(num);
    }

    @Override
    public void close() {
        pw.close();
    }
}
