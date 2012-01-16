package com.google.code.java.core.autoboxing;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author peter.lawrey
 */
public class AutoBoxedWeakKeysMain {
    public static void main(String... args) {
        Map<Long, String> keyValueMap = new WeakHashMap<Long, String>(10000);
        for (long i = 1; i <= 8192; i *= 2)
            keyValueMap.put(i, "key-" + i);
        System.out.println("Before GC: " + keyValueMap);
        System.gc();
        System.out.println("After GC: " + keyValueMap);
    }
}
