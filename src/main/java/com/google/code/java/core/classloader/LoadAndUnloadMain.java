/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.classloader;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

public class LoadAndUnloadMain {
  public static void main(String... args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InterruptedException {
    URL url = LoadAndUnloadMain.class.getProtectionDomain().getCodeSource().getLocation();
    final String className = LoadAndUnloadMain.class.getPackage().getName() + ".UtilityClass";
    {
      ClassLoader cl;
      Class clazz;

      for (int i = 0; i < 2; i++) {
        cl = new CustomClassLoader(url);
        clazz = cl.loadClass(className);
        loadClass(clazz);

        cl = new CustomClassLoader(url);
        clazz = cl.loadClass(className);
        loadClass(clazz);
        triggerGC();
      }
    }
    triggerGC();
  }

  private static void triggerGC() throws InterruptedException {
    System.out.println("\n-- Starting GC");
    System.gc();
    Thread.sleep(100);
    System.out.println("-- End of GC\n");
  }

  private static void loadClass(Class clazz) throws NoSuchFieldException, IllegalAccessException {
    final Field id = clazz.getDeclaredField("ID");
    id.setAccessible(true);
    id.get(null);
  }

  private static class CustomClassLoader extends URLClassLoader {
    public CustomClassLoader(URL url) {
      super(new URL[]{url}, null);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      try {
        return super.loadClass(name, resolve);
      } catch (ClassNotFoundException e) {
        return Class.forName(name, resolve, LoadAndUnloadMain.class.getClassLoader());
      }
    }

  }
}

class UtilityClass {
  static final String ID = Integer.toHexString(System.identityHashCode(UtilityClass.class));
  private static final Object FINAL = new Object() {
    @Override
    protected void finalize() throws Throwable {
      super.finalize();
      System.out.println(ID + " Finalized.");
    }
  };

  static {
    System.out.println(ID + " Initialising");
  }
}
