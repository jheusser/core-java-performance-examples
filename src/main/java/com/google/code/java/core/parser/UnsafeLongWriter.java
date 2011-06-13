package com.google.code.java.core.parser;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 12/06/11
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class UnsafeLongWriter implements LongWriter {
    private long address;

    public UnsafeLongWriter(long address) {
        this.address = address;
    }

    @Override
    public void write(long num) throws IOException {
        ParserUtils.UNSAFE.putLong(address, num);
        address += 8;
    }

    @Override
    public void close() {

    }
}
