package com.google.code.java.core.sizeof;

import org.junit.Test;

import java.util.BitSet;

public class MemoryUsageExamplesTest {
    @Test
    public void testStringPlus() {
        System.out.printf("The average memory used by StringBuilder for \"abc\" + \"def\" is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                StringBuilder a2f = new StringBuilder().append("abc").append("def");
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by a new \"abcdef\" String is %.1f bytes%n", new SizeofUtil() {
            char[] chars = "abcdef".toCharArray();

            @Override
            protected int create() {
                String a2f = new String(chars);
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by \"abc\" + \"def\" is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                String a2c = "abc";
                String a2f = a2c + "def";
                return 1;
            }
        }.averageBytes());
    }

    @Test
    public void testArray() {
        System.out.printf("The average memory used by new BitSet(1024) is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                BitSet array = new BitSet(1024);
                for (int i = 0; i < array.length(); i++) array.set(i, i % 2 == 0);
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by boolean[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                boolean[] array = new boolean[1024];
                for (int i = 0; i < array.length; i++) array[i] = i % 2 == 0;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Boolean[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Boolean[] array = new Boolean[1024];
                for (int i = 0; i < array.length; i++) array[i] = i % 2 == 0;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by byte[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                byte[] array = new byte[1024];
                for (int i = 0; i < array.length; i++) array[i] = (byte) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Byte[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Byte[] array = new Byte[1024];
                for (int i = 0; i < array.length; i++) array[i] = (byte) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by char[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                char[] array = new char[1024];
                for (int i = 0; i < array.length; i++) array[i] = (char) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Character[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Character[] array = new Character[1024];
                for (int i = 0; i < array.length; i++) array[i] = (char) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by short[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                short[] array = new short[1024];
                for (int i = 0; i < array.length; i++) array[i] = (short) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Short[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Short[] array = new Short[1024];
                for (int i = 0; i < array.length; i++) array[i] = (short) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by int[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                int[] ints = new int[1024];
                for (int i = 0; i < ints.length; i++) ints[i] = i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Integer[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Integer[] ints = new Integer[1024];
                for (int i = 0; i < ints.length; i++) ints[i] = i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by float[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                float[] array = new float[1024];
                for (int i = 0; i < array.length; i++) array[i] = (float) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Float[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Float[] array = new Float[1024];
                for (int i = 0; i < array.length; i++) array[i] = (float) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by long[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                long[] array = new long[1024];
                for (int i = 0; i < array.length; i++) array[i] = (long) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Long[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Long[] array = new Long[1024];
                for (int i = 0; i < array.length; i++) array[i] = (long) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by double[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                double[] array = new double[1024];
                for (int i = 0; i < array.length; i++) array[i] = (double) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by Double[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                Double[] array = new Double[1024];
                for (int i = 0; i < array.length; i++) array[i] = (double) i;
                return 1;
            }
        }.averageBytes());
        System.out.printf("The average memory used by String[1024] is %.1f bytes%n", new SizeofUtil() {
            @Override
            protected int create() {
                String[] array = new String[1024];
                for (int i = 0; i < array.length; i++) array[i] = String.valueOf(i);
                return 1;
            }
        }.averageBytes());
    }
}
