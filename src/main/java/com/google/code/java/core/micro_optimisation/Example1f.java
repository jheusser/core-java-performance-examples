/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.micro_optimisation;

public class Example1f {
    public static void main(String... args) throws Exception {
        long start = 0;
        final int runs = 100000000;
        String hi_all = null;
        for (int i = -11000; i < runs; i++) {
            if (i == 0) start = System.nanoTime();
            String hi = "Hello";
            String all = "World";
            hi_all = hi + ' ' + all + '!';
        }
        long time = System.nanoTime() - start;
        System.out.printf("Creating " + hi_all + " took an average of" +
                " %.2f ns%n", (double) time / runs);
    }
}
