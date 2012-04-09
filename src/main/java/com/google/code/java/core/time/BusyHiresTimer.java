package com.google.code.java.core.time;

/**
 * @author peter.lawrey
 */
public enum BusyHiresTimer {
    ;
    private static volatile long counter;
    private static int factor;

    static {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted())
                    counter++;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static long nanoTime() {
        if (factor == 0) {
            init();
            init();
        }
        return (counter * factor) >> 8;
    }

    private static void init() {
        while (counter < 1)
            Thread.yield();
        long start0 = System.nanoTime();
        long start;
        while ((start = System.nanoTime()) == start0) ;
        long counter1 = counter;
        if (counter1 == 0) throw new IllegalStateException("thread not started.");
        long end0 = start + 50 * 1000 * 1000; // 50 ms
        long end;
        while ((end = System.nanoTime()) < end0) ;
        long counter2 = counter;
        factor = (int) (((end - start) << 8) / (counter2 - counter1));
        System.out.printf("Each count takes %.2f ns%n", factor / 256.0);
    }
}
