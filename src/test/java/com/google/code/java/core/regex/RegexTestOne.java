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

public class RegexTestOne {
    public static void main(String... args) {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2431604</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2430868</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/Wireless-Carrier-Reps-Past-Present-2430807</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2430694</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2430575</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2431452</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432377</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2428508</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432379</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432380</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432381</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432383</loc><changefreq>monthly</changefreq></url>\n" +
                "<url><loc>http://www.linkedin.com/groups/gid-2432384</loc><changefreq>monthly</changefreq></url>\n" +
                "</urlset>\n";
        for (int runs : new int[]{1, 10, 100, 1000, 10000, 100000}) {
            long time1 = timeRegexFind(text, "<url>\\s*<loc>", runs);
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
            while (matcher.find()) count++;
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
            while ((pos = text.indexOf("<url>", pos)) >= 0) {
                pos += "<url>".length();
                while (Character.isWhitespace(text.charAt(pos)))
                    pos++;
                if (text.startsWith("<loc>", pos)) {
                    count++;
                    pos += "<loc>".length();
                }
            }
        }
        long time = System.nanoTime() - start;
        // System.out.println("IndexOf found " + count + " matches, took an average of " + time / runs / 1e3 + " micro-seconds looping "+runs+" times");
        return time / runs;
    }
}
