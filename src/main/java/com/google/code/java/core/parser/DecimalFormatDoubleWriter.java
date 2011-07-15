package com.google.code.java.core.parser;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class DecimalFormatDoubleWriter implements DoubleWriter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private final PrintWriter pw;

    public DecimalFormatDoubleWriter(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void write(double num) {
        pw.println(DECIMAL_FORMAT.format(num));
    }

    @Override
    public void close() {
        pw.close();
    }
}
