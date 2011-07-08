package com.google.code.java.core.primitives;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Report {
    final double[] usedMB, timeFromStart, avgTime;
    int count;

    public Report(int capacity) {
        usedMB = new double[capacity];
        timeFromStart = new double[capacity];
        avgTime = new double[capacity];
    }

    void print(String filename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        for (int i = 0; i < count; i++)
            out.printf("%.1f, Took %.1f ns per loop, free MB, %.1f%n", timeFromStart[i], avgTime[i], usedMB[i]);
        out.close();

    }
}
