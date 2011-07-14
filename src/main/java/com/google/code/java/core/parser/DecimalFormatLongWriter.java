package com.google.code.java.core.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class DecimalFormatLongWriter implements LongWriter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");
    private final PrintWriter pw;

    public DecimalFormatLongWriter(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void write(long num) throws IOException {
        pw.println(DECIMAL_FORMAT.format(num));
    }

    @Override
    public void close() {
        pw.close();
    }
}
