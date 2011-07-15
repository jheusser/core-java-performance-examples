package com.google.code.java.core.javac.generated;


public class PrivateMethod {
    private void init() {
        throw new UnsupportedOperationException();
    }

    ;

    class Inner {
        Inner() {
            init();
        }
    }

    public static void main(String... args) {
        PrivateMethod pm = new PrivateMethod();
        Inner inner = pm.new Inner();
    }
}
