package com.google.code.java.core.threads;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author peter.lawrey
 */
public class ThreadedAccountMain {
    public static void main(String... args) {
        int accountCount = 20 * 1000;
        Account[] accounts = new Account[accountCount];
        for (int i = 0; i < accounts.length; i++) accounts[i] = new Account(100);
        for (int r = 0; r < 9; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < accountCount; i += 2) {
                transfer(accounts[i], accounts[i + 1], 10);
                transfer(accounts[i + 1], accounts[i], 11);
            }
            long time = System.nanoTime() - start;
            System.out.printf("Took an average of %.1f ns to transfer money with a synchronized on each Account.%n", (double) time / accountCount);
        }
    }

    private static void transfer(Account from, Account to, int amount) {
        boolean comp = from.compareTo(to) <= 0;
        Object firstLock = comp ? from.lock() : to.lock();
        Object secondLock = comp ? to.lock() : from.lock();
        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.balance() < amount) throw new IllegalArgumentException();
                from.transfer(-amount);
                to.transfer(amount);
            }
        }
    }


    static class Account implements Comparable<Account> {
        private static final AtomicLong IDS = new AtomicLong();
        private final long id = IDS.getAndIncrement();
        private final Object lock = new Object();

        private long balance;

        public Account(long balance) {
            this.balance = balance;
        }

        public Object lock() {
            return lock;
        }

        public long balance() {
            return balance;
        }

        public void transfer(long delta) {
            if (delta + balance < 0) throw new IllegalStateException();
            balance += delta;
        }

        @Override
        public int compareTo(Account o) {
            return id > o.id ? +1 : -1;
        }
    }
}
