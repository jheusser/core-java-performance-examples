package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class DecimalFormatLongReader implements LongReader {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");
    private final BufferedReader br;

    public DecimalFormatLongReader(BufferedReader br) {
        this.br = br;
    }

    @Override
    public long read() throws IOException {
        String num = null;
        try {
            num = br.readLine();
            return DECIMAL_FORMAT.parse(num).longValue();
        } catch (ParseException e) {
            throw new IOException("Unable to parse '" + num + '\'', e);
        }
    }

    @Override
    public void close() {
        ParserUtils.close(br);
    }
}
