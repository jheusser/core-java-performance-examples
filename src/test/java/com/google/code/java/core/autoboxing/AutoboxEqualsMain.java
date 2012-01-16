package com.google.code.java.core.autoboxing;

/**
 * @author peter.lawrey
 */
public class AutoboxEqualsMain {
    public static void main(String... args) {
        findAutoBoxedBoolean();
        findAutoBoxedBytes();
        findAutoBoxedChars();
        findAutoBoxedShort();
        findAutoBoxedIntegers();
        findAutoBoxedLong();
        findAutoBoxedFloat();
        findAutoBoxedDouble();
    }

    private static void findAutoBoxedBoolean() {
        Boolean t1 = true;
        Boolean t2 = true;
        Boolean f1 = false;
        Boolean f2 = false;

        if (t1 == t2 && f1 == f2)
            System.out.println("All Boolean are cached.");
        else
            System.out.println("Not all Boolean are cached");
    }

    private static void findAutoBoxedBytes() {
        byte first = 1, last = -1;
        for (int i = 0; i >= Byte.MIN_VALUE; i--) {
            Byte b1 = (byte) i;
            Byte b2 = (byte) i;
            if (b1 == b2)
                first = (byte) i;
            else break;
        }
        for (int i = 0; i <= Byte.MAX_VALUE; i++) {
            Byte b1 = (byte) i;
            Byte b2 = (byte) i;
            if (b1 == b2)
                last = (byte) i;
            else break;
        }
        if (first == Byte.MIN_VALUE && last == Byte.MAX_VALUE)
            System.out.println("All Byte are cached.");
        else
            System.out.println("The first auto-boxed byte is " + first + " and the last is " + last);
    }

    private static void findAutoBoxedChars() {
        char first = 1, last = 0;
        for (int i = 0; i >= Character.MIN_VALUE; i--) {
            Character b1 = (char) i;
            Character b2 = (char) i;
            if (b1 == b2)
                first = (char) i;
            else break;
        }
        for (int i = 0; i <= Character.MAX_VALUE; i++) {
            Character b1 = (char) i;
            Character b2 = (char) i;
            if (b1 == b2)
                last = (char) i;
            else break;
        }
        if (first == Character.MIN_VALUE && last == Character.MAX_VALUE)
            System.out.println("All Char are cached.");
        else
            System.out.println("The first auto-boxed char is " + (int) first + " and the last is " + (int) last);
    }

    private static void findAutoBoxedShort() {
        short first = 1, last = -1;
        for (int i = 0; i >= Short.MIN_VALUE; i--) {
            Short b1 = (short) i;
            Short b2 = (short) i;
            if (b1 == b2)
                first = (short) i;
            else break;
        }
        for (int i = 0; i <= Short.MAX_VALUE; i++) {
            Short b1 = (short) i;
            Short b2 = (short) i;
            if (b1 == b2)
                last = (short) i;
            else break;
        }
        if (first == Short.MIN_VALUE && last == Short.MAX_VALUE)
            System.out.println("All Short are cached.");
        else
            System.out.println("The first auto-boxed short is " + first + " and the last is " + last);
    }

    private static void findAutoBoxedIntegers() {
        int first = 1, last = -1;
        for (long i = 0; i >= Integer.MIN_VALUE; i--) {
            Integer b1 = (int) i;
            Integer b2 = (int) i;
            if (b1 == b2)
                first = (int) i;
            else break;
        }
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            Integer b1 = (int) i;
            Integer b2 = (int) i;
            if (b1 == b2)
                last = (int) i;
            else break;
        }
        if (first == Integer.MIN_VALUE && last == Integer.MAX_VALUE)
            System.out.println("All Integer are cached.");
        else
            System.out.println("The first auto-boxed int is " + first + " and the last is " + last);
    }

    private static void findAutoBoxedLong() {
        long first = 1, last = -1;
        for (int i = 0; i > Long.MIN_VALUE; i--) {
            Long b1 = (long) i;
            Long b2 = (long) i;
            if (b1 == b2)
                first = (long) i;
            else break;
        }
        for (int i = 0; i < Long.MAX_VALUE; i++) {
            Long b1 = (long) i;
            Long b2 = (long) i;
            if (b1 == b2)
                last = (long) i;
            else break;
        }
        if (first == Long.MIN_VALUE && last == Long.MAX_VALUE)
            System.out.println("All Long are cached.");
        else
            System.out.println("The first auto-boxed long is " + first + " and the last is " + last);
    }

    private static void findAutoBoxedFloat() {
        Float f1 = 0.0f;
        Float f2 = 0.0f;
        if (f1 == f2)
            System.out.println("Some Float are cached.");
        else
            System.out.println("No Float are cached.");
    }

    private static void findAutoBoxedDouble() {
        Double f1 = 0.0;
        Double f2 = 0.0;
        if (f1 == f2)
            System.out.println("Some Double are cached.");
        else
            System.out.println("No Double are cached.");
    }
}
