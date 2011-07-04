package com.google.code.java.core.classloader;

public class ShortClassLoadingMain {
    public static void main(String... args) {
        System.out.println("Start");
        Class aClass = AClass.class;
        System.out.println("Loaded");
        String s = AClass.ID;
        System.out.println("Initialised");
    }
}

class AClass {
    static final String ID;

    static {
        System.out.println("AClass: Initialising");
        ID = "ID";
    }
}
