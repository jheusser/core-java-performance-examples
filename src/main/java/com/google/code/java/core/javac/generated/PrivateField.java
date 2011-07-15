package com.google.code.java.core.javac.generated;

public class PrivateField {
    private int num = 0;

    public class Inner {
        public void set(int n) {
            num = n;
        }

        public int get() {
            return num;
        }

        public void increment() {
            num++;
        }

        public void multiply(int n) {
            num *= n;
        }
    }
}
