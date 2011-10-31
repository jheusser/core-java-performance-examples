/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.micro_optimisation;

public class Example1b {
    public static void main(String... args) throws Exception {
        long start = System.nanoTime();
        final String hi = "Hello";
        final String all = "World";
        String hi_all = hi + ' ' + all + '!';
        long time = System.nanoTime() - start;
        System.out.printf("Creating " + hi_all + " took %,d ns%n", time);
    }
}
