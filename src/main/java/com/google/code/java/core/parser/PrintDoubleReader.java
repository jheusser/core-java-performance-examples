package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;

public class PrintDoubleReader implements DoubleReader {
    private final BufferedReader br;

    public PrintDoubleReader(BufferedReader br) {
        this.br = br;
    }

    @Override
    public double read() throws IOException, NumberFormatException {
        return Double.parseDouble(br.readLine());
    }

    @Override
    public void close() {
        ParserUtils.close(br);
    }
}
