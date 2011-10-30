/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.gc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DoubleCompareMain {
    public static void main(String... args) {
        List<ObjectWithDouble> events = new ArrayList<ObjectWithDouble>();
        for (int i = 0; i < 10000000; i++)
            events.add(new ObjectWithDouble(Math.random()));

        doTest(events, Comparators.PRIMITIVE);
        doTest(events, Comparators.OBJECT);
    }

    private static double doTest(List<ObjectWithDouble> events, Comparators comparator) {
        long start = System.nanoTime();
        PriorityQueue<ObjectWithDouble> pq = new PriorityQueue<ObjectWithDouble>(events.size(), comparator);
        pq.addAll(events);
        double sum = 0;
        while (!pq.isEmpty())
            sum += pq.poll().time;
        long time = System.nanoTime() - start;
        System.out.println(comparator + ": Took an average of " + time / events.size());
        return sum;
    }
}

enum Comparators implements Comparator<ObjectWithDouble> {
    PRIMITIVE {
        @Override
        public int compare(ObjectWithDouble o1, ObjectWithDouble o2) {
            return Double.compare(o1.time, o2.time);
        }
    }, OBJECT {
        @Override
        public int compare(ObjectWithDouble o1, ObjectWithDouble o2) {
            return new Double(o1.time).compareTo(o2.time);
        }
    }
}

class ObjectWithDouble {
    final double time;

    ObjectWithDouble(double time) {
        this.time = time;
    }
}