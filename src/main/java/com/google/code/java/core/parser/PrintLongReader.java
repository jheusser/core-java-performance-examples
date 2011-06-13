package com.google.code.java.core.parser;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 12/06/11
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
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
