package com.google.code.java.core.parser;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ParserUtilsTest {
    @Test
    public void testDigits() {
        long l = (long) 1e18;
        for (int i = 19; i > 0; i--) {
            assertEquals(i, ParserUtils.digits(l));
            assertEquals(i - 1, ParserUtils.digits(l - 1));
            l /= 10;
        }
    }
}
