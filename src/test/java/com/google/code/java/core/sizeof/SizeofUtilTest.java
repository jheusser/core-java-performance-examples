package com.google.code.java.core.sizeof;

import org.junit.Test;

import java.util.BitSet;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.assertEquals;

public class SizeofUtilTest {
    @Test
    public void testAverageBytes() throws Exception {
        assertEquals(4.0, new SizeofUtil() {
            int[] ints;

            @Override
            protected int create() {
                ints = new int[1024];
                return ints.length;
            }
        }.averageBytes(), 0.02);

        assertEquals(1.0 / 8, new SizeofUtil() {
            BitSet bits;

            @Override
            protected int create() {
                bits = new BitSet(1024 * 1024);
                return bits.size();
            }
        }.averageBytes(), 1e-4);
    }

    @Test
    public void testHeaderSize() {
        System.out.printf("The average size of an Integer is %.1f bytes%n", new SizeofUtil() {
            Integer[] ints = new Integer[128 * 1024];

            @Override
            protected int create() {
                for (int i = 0; i < ints.length; i++)
                    ints[i] = new Integer(i);
                return ints.length;
            }
        }.averageBytes());
        System.out.printf("The average size of an Long is %.1f bytes%n", new SizeofUtil() {
            Long[] ints = new Long[128 * 1024];

            @Override
            protected int create() {
                for (int i = 0; i < ints.length; i++)
                    ints[i] = new Long(i);
                return ints.length;
            }
        }.averageBytes());
        System.out.printf("The average size of an AtomicReference is %.1f bytes%n", new SizeofUtil() {
            AtomicReference[] ints = new AtomicReference[128 * 1024];

            @Override
            protected int create() {
                for (int i = 0; i < ints.length; i++)
                    ints[i] = new AtomicReference(null);
                return ints.length;
            }
        }.averageBytes());
        System.out.printf("The average size of an Calendar is %.1f bytes%n", new SizeofUtil() {
            Calendar[] ints = new Calendar[1024];

            @Override
            protected int create() {
                for (int i = 0; i < ints.length; i++)
                    ints[i] = Calendar.getInstance();
                return ints.length;
            }
        }.averageBytes());
    }
}
