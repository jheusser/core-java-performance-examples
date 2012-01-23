package com.google.code.java.core.threads;

/**
 * @author peter.lawrey
 */
public class RequiresVolatileMain {
    static boolean value;

    public static void main(String... args) {
        new Thread(new MyRunnable(true), "Sets true").start();
        new Thread(new MyRunnable(false), "Sets false").start();
    }

    private static class MyRunnable implements Runnable {
        private final boolean target;

        private MyRunnable(boolean target) {
            this.target = target;
        }

        @Override
        public void run() {
            int count = 0;
            boolean logged = false;
            while (true) {
                if (value != target) {
                    value = target;
                    count = 0;
                    if (!logged)
                        System.out.println(Thread.currentThread().getName() + ": reset value=" + value);
                } else if (++count % 1000000000 == 0) {
                    System.out.println(Thread.currentThread().getName() + ": value=" + value + " target=" + target);
                    logged = true;
                }
            }
        }
    }
}
