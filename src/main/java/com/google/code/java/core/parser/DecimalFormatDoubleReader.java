package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class DecimalFormatDoubleReader implements DoubleReader {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private final BufferedReader br;

    public DecimalFormatDoubleReader(BufferedReader br) {
        this.br = br;
    }

    @Override
    public double read() throws IOException {
        String num = null;
        try {
            num = br.readLine();
            return DECIMAL_FORMAT.parse(num).doubleValue();
        } catch (ParseException e) {
            throw new IOException("Unable to parse '" + num + '\'', e);
        }
    }

    @Override
    public void close() {
        ParserUtils.close(br);
    }
}
