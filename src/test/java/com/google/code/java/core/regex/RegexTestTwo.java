/*
 * Copyright (c) 2012.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestTwo {
    public static void main(String... args) {
        String text = "name=\"rating_count\" value=\"41\"";
        for (int runs : new int[]{1, 10, 100, 1000, 10000, 100000, 100000, 100000}) {
            long time1 = timeRegexFind(text, "(?:value=\\x22)([^\\x22]+)", runs);
            long time2 = timeIndexOfFind(text, runs);
            System.out.printf("Performing %,d loops, regex took %.3f us and indexOf took %.3f us on average, ratio=%.1f%n",
                    runs, time1 / 1e3, time2 / 1e3, (double) time1 / time2);
        }
    }

    private static long timeRegexFind(String text, String find, int runs) {
        Pattern regex = Pattern.compile(find);
        int count = 0;
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            count = 0;
            Matcher matcher = regex.matcher(text);
            while (matcher.find()) {
                String value = matcher.group(1);
                assert "41".equals(value);
                count++;
            }
        }
        long time = System.nanoTime() - start;
        // System.out.println("Regex found " + count + " matches, took an average of " + time / runs / 1e3 + " micro-seconds looping "+runs+" times");
        return time / runs;
    }

    private static long timeIndexOfFind(String text, int runs) {
        int count = 0;
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            count = 0;
            int pos = 0;
            while ((pos = text.indexOf("value=\"", pos)) >= 0) {
                pos += "value=\"".length();
                int end = text.indexOf('"', pos);
                String value = text.substring(pos, end);
                assert "41".equals(value);
                pos = end + 1;
                count++;
            }
        }
        long time = System.nanoTime() - start;
//        System.out.println("IndexOf found " + count + " matches, took an average of " + time / runs / 1e3 + " micro-seconds looping "+runs+" times");
        return time / runs;
    }
}
