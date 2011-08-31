/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.methodhandles;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;

public class MethodHandleMain {
  public static void main(String... args) throws Throwable {
    directCallTest();
    reflectionTest();
    methodHandleTest();
  }

  private static long methodHandleTest() throws Throwable {
    final Lookup lookup = lookup();
    MethodHandle multiply = lookup.findStatic(MethodHandleMain.class, "multiply", methodType(int.class, int.class, int.class));
    MethodHandle quadruple = insertArguments(multiply, 1, 4);

    System.out.println(multiply.invoke(3, 2)); // prints 6
    System.out.println(quadruple.invoke(5)); // prints 20

    MethodHandle subtract = lookup.findStatic(MethodHandleMain.class, "subtract", methodType(int.class, int.class, int.class));
    MethodHandle subtractFromFour = insertArguments(subtract, 0, 4);
    MethodHandle fourLess = insertArguments(subtract, 1, 4);
    System.out.println(subtract.invoke(10, 5)); // prints 5
    System.out.println(subtractFromFour.invoke(10)); // prints -6
    System.out.println(fourLess.invoke(10)); // prints 6

    long sum = 0;
    final int runs = 100000;
    long start = System.nanoTime();
    for (int i = 0; i < runs; i += 5) {
      sum += (Integer) multiply.invoke(i, i) +
                 (Integer) quadruple.invoke(i) +
                 (Integer) subtract.invoke(i, 1) +
                 (Integer) subtractFromFour.invoke(i) +
                 (Integer) fourLess.invoke(i);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Method Handle Average call time was %,d%n", time / runs);
    return sum;
  }

  private static long reflectionTest() throws Throwable {
    Method multiply = MethodHandleMain.class.getDeclaredMethod("multiply", int.class, int.class);

    System.out.println(multiply.invoke(null, 3, 2));

    Method subtract = MethodHandleMain.class.getDeclaredMethod("subtract", int.class, int.class);
    System.out.println(subtract.invoke(null, 10, 5));

    long sum = 0;
    final int runs = 100000;
    long start = System.nanoTime();
    for (int i = 0; i < runs; i += 5) {
      sum += (Integer) multiply.invoke(null, i, i) +
                 (Integer) multiply.invoke(null, 4, i) +
                 (Integer) subtract.invoke(null, i, 1) +
                 (Integer) subtract.invoke(null, 4, i) +
                 (Integer) subtract.invoke(null, i, 4);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Method Average call time was %,d%n", time / runs);
    return sum;
  }

  private static long directCallTest() throws Throwable {
    System.out.println(multiply(3, 2));

    System.out.println(subtract(10, 5));

    long sum = 0;
    final int runs = 100000;
    long start = System.nanoTime();
    for (int i = 0; i < runs; i += 5) {
      sum += multiply(i, i) +
                 multiply(4, i) +
                 subtract(i, 1) +
                 subtract(4, i) +
                 subtract(i, 4);
    }
    long time = System.nanoTime() - start;
    System.out.printf("Direct call Average call time was %,d%n", time / runs);
    return sum;
  }

  public static int multiply(int i, int j) {
    return i * j;
  }

  public static int subtract(int i, int j) {
    return i - j;
  }
}
