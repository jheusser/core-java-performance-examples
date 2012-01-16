package com.google.code.java.core.autoboxing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author peter.lawrey
 */
public class CompareIntArrayMain {
    public static void main(String... args) {
        long used1 = usedMemory();
        int[] ints1 = new int[16000];
        for (int i = 0; i < ints1.length; i++)
            ints1[i] = i;
        long used2 = usedMemory();
        List<Integer> ints2 = new ArrayList<Integer>(ints1.length);
        for (int i = 0; i < ints1.length; i++)
            ints2.add(i);
        long used3 = usedMemory();
        if (used3 - used1 < 80000)
            System.out.println("You must use -XX:-UseTLAB to see small memory allocations");
        System.out.println("The int[" + ints1.length + "] took " + (used2 - used1) + " bytes and new ArrayList<Integer>() with " + ints2.size() + " values took " + (used3 - used2) + " bytes");

    }

    public static long usedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
