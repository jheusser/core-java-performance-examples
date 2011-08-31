/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.shootout;

/* The Computer Language Benchmarks Game

   http://shootout.alioth.debian.org/

   contributed by Mark C. Lewis
   modified slightly by Chad Whipkey
*/
// run with: java  -server -XX:+TieredCompilation -XX:+AggressiveOpts nbody 50000000

public final class nbody {
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);

    long start = System.nanoTime();
    NBodySystem bodies = new NBodySystem();
    System.out.printf("%.9f\n", bodies.energy());
    for (int i = 0; i < n; ++i)
      bodies.advance(0.01);
    System.out.printf("%.9f\n", bodies.energy());
    long time = System.nanoTime() - start;
    System.out.printf("Took %.3f seconds to run%n", time / 1e9);
  }
}

final class NBodySystem {
  private Body[] bodies;

  public NBodySystem() {
    bodies = Body.values();

    double px = 0.0;
    double py = 0.0;
    double pz = 0.0;
    for (Body body : bodies) {
      px += body.vx * body.mass;
      py += body.vy * body.mass;
      pz += body.vz * body.mass;
    }
    bodies[0].offsetMomentum(px, py, pz);
  }

  public void advance(double dt) {

    for (int i = 0; i < bodies.length; ++i) {
      Body iBody = bodies[i];
      for (int j = i + 1; j < bodies.length; ++j) {
        final Body body = bodies[j];
        double dx = iBody.x - body.x;
        double dy = iBody.y - body.y;
        double dz = iBody.z - body.z;

        double dSquared = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(dSquared);
        double mag = dt / (dSquared * distance);

        iBody.vx -= dx * body.mass * mag;
        iBody.vy -= dy * body.mass * mag;
        iBody.vz -= dz * body.mass * mag;

        body.vx += dx * iBody.mass * mag;
        body.vy += dy * iBody.mass * mag;
        body.vz += dz * iBody.mass * mag;
      }
    }

    for (Body body : bodies) {
      body.x += dt * body.vx;
      body.y += dt * body.vy;
      body.z += dt * body.vz;
    }
  }

  public double energy() {
    double dx, dy, dz, distance;
    double e = 0.0;

    for (int i = 0; i < bodies.length; ++i) {
      Body iBody = bodies[i];
      e += 0.5 * iBody.mass *
               (iBody.vx * iBody.vx
                    + iBody.vy * iBody.vy
                    + iBody.vz * iBody.vz);

      for (int j = i + 1; j < bodies.length; ++j) {
        Body jBody = bodies[j];
        dx = iBody.x - jBody.x;
        dy = iBody.y - jBody.y;
        dz = iBody.z - jBody.z;

        distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        e -= (iBody.mass * jBody.mass) / distance;
      }
    }
    return e;
  }
}


enum Body {
  SUN {{
    mass = SOLAR_MASS;
  }},
  JUPITER {{
    x = 4.84143144246472090e+00;
    y = -1.16032004402742839e+00;
    z = -1.03622044471123109e-01;
    vx = 1.66007664274403694e-03 * DAYS_PER_YEAR;
    vy = 7.69901118419740425e-03 * DAYS_PER_YEAR;
    vz = -6.90460016972063023e-05 * DAYS_PER_YEAR;
    mass = 9.54791938424326609e-04 * SOLAR_MASS;
  }},
  SATURN {{
    x = 8.34336671824457987e+00;
    y = 4.12479856412430479e+00;
    z = -4.03523417114321381e-01;
    vx = -2.76742510726862411e-03 * DAYS_PER_YEAR;
    vy = 4.99852801234917238e-03 * DAYS_PER_YEAR;
    vz = 2.30417297573763929e-05 * DAYS_PER_YEAR;
    mass = 2.85885980666130812e-04 * SOLAR_MASS;
  }},
  URANUS {{
    x = 1.28943695621391310e+01;
    y = -1.51111514016986312e+01;
    z = -2.23307578892655734e-01;
    vx = 2.96460137564761618e-03 * DAYS_PER_YEAR;
    vy = 2.37847173959480950e-03 * DAYS_PER_YEAR;
    vz = -2.96589568540237556e-05 * DAYS_PER_YEAR;
    mass = 4.36624404335156298e-05 * SOLAR_MASS;
  }},
  NEPTUNE {{
    x = 1.53796971148509165e+01;
    y = -2.59193146099879641e+01;
    z = 1.79258772950371181e-01;
    vx = 2.68067772490389322e-03 * DAYS_PER_YEAR;
    vy = 1.62824170038242295e-03 * DAYS_PER_YEAR;
    vz = -9.51592254519715870e-05 * DAYS_PER_YEAR;
    mass = 5.15138902046611451e-05 * SOLAR_MASS;
  }};

  static final double PI = 3.141592653589793;
  static final double SOLAR_MASS = 4 * PI * PI;
  // NOTE: there are 365.2425 days in the gregorian calender
  static final double DAYS_PER_YEAR = 365.24;

  public double x, y, z, vx, vy, vz, mass;

  Body offsetMomentum(double px, double py, double pz) {
    vx = -px / SOLAR_MASS;
    vy = -py / SOLAR_MASS;
    vz = -pz / SOLAR_MASS;
    return this;
  }
}
