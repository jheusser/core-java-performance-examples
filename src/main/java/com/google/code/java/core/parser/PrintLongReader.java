package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;

public class PrintLongReader implements LongReader {
    private final BufferedReader br;

    public PrintLongReader(BufferedReader br) {
        this.br = br;
    }

    @Override
    public long read() throws IOException {
        return Long.parseLong(br.readLine());
    }

    @Override
    public void close() {
        ParserUtils.close(br);
    }
}
