/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.memory;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class MemoryAlignment {
  public static void main(String... args) throws NoSuchFieldException, IllegalAccessException {
    {
      OneInt r0 = new OneInt(); // load the class.
      long free1 = freeMemory();
      OneInt r1 = new OneInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with one int reserved " + (free1 - free2) + " bytes.");
    }
    {
      TwoInt r0 = new TwoInt(); // load the class.
      long free1 = freeMemory();
      TwoInt r1 = new TwoInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with two int reserved " + (free1 - free2) + " bytes.");
    }
    {
      ThreeInt r0 = new ThreeInt(); // load the class.
      long free1 = freeMemory();
      ThreeInt r1 = new ThreeInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with three int reserved " + (free1 - free2) + " bytes.");
    }
    {
      FourInt r0 = new FourInt(); // load the class.
      long free1 = freeMemory();
      FourInt r1 = new FourInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with four int reserved " + (free1 - free2) + " bytes.");
    }
    {
      FiveInt r0 = new FiveInt(); // load the class.
      long free1 = freeMemory();
      FiveInt r1 = new FiveInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with five int reserved " + (free1 - free2) + " bytes.");
    }
    {
      SixInt r0 = new SixInt(); // load the class.
      long free1 = freeMemory();
      SixInt r1 = new SixInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with six int reserved " + (free1 - free2) + " bytes.");
    }
    {
      SevenInt r0 = new SevenInt(); // load the class.
      long free1 = freeMemory();
      SevenInt r1 = new SevenInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with seven int reserved " + (free1 - free2) + " bytes.");
    }
    {
      EightInt r0 = new EightInt(); // load the class.
      long free1 = freeMemory();
      EightInt r1 = new EightInt();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with eight int reserved " + (free1 - free2) + " bytes.");
    }

    {
      OneRef r0 = new OneRef(); // load the class.
      long free1 = freeMemory();
      OneRef r1 = new OneRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with one ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      TwoRef r0 = new TwoRef(); // load the class.
      long free1 = freeMemory();
      TwoRef r1 = new TwoRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with two ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      ThreeRef r0 = new ThreeRef(); // load the class.
      long free1 = freeMemory();
      ThreeRef r1 = new ThreeRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with three ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      FourRef r0 = new FourRef(); // load the class.
      long free1 = freeMemory();
      FourRef r1 = new FourRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with four ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      FiveRef r0 = new FiveRef(); // load the class.
      long free1 = freeMemory();
      FiveRef r1 = new FiveRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with five ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      SixRef r0 = new SixRef(); // load the class.
      long free1 = freeMemory();
      SixRef r1 = new SixRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with six ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      SevenRef r0 = new SevenRef(); // load the class.
      long free1 = freeMemory();
      SevenRef r1 = new SevenRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with seven ref reserved " + (free1 - free2) + " bytes.");
    }
    {
      EightRef r0 = new EightRef(); // load the class.
      long free1 = freeMemory();
      EightRef r1 = new EightRef();
      long free2 = freeMemory();
      if (free2 == free1) throw new Error("You must run this with -XX:-UseTLAB.");
      System.out.println("Class with eight ref reserved " + (free1 - free2) + " bytes.");
    }
    for (int i = 0; i <= 64; i++)
      for (int n = 0; n < 3; n++) {
        Field address = Buffer.class.getDeclaredField("address");
        address.setAccessible(true);
        ByteBuffer bb1 = ByteBuffer.allocateDirect(i);
        ByteBuffer bb2 = ByteBuffer.allocateDirect(i);
        long address1 = (Long) address.get(bb1);
        long address2 = (Long) address.get(bb2);
        System.out.println("Allocating " + i + " bytes of direct memory reserves " + (address2 - address1) + " bytes");
      }

  }

  public static long freeMemory() {
    return Runtime.getRuntime().freeMemory();
  }
}

class OneInt {
  int i;
}

class TwoInt {
  int i, j;
}

class ThreeInt {
  int i, j, k;
}

class FourInt {
  int i, j, k, l;
}

class FiveInt {
  int i, j, k, l, m;
}

class SixInt {
  int i, j, k, l, m, n;
}

class SevenInt {
  int i, j, k, l, m, n, o;
}

class EightInt {
  int i, j, k, l, m, n, o, p;
}

class OneRef {
  Object i;
}

class TwoRef {
  Object i, j;
}

class ThreeRef {
  Object i, j, k;
}

class FourRef {
  Object i, j, k, l;
}

class FiveRef {
  Object i, j, k, l, m;
}

class SixRef {
  Object i, j, k, l, m, n;
}

class SevenRef {
  Object i, j, k, l, m, n, o;
}

class EightRef {
  Object i, j, k, l, m, n, o, p;
}
