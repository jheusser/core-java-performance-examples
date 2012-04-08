package com.google.code.java.core.math;
/*
   Copyright 2011 Peter Lawrey

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
/*
In Java 6 prints
Math.round(0.4999999999999999167332731531132594682276248931884765625000000000000000000000000000000000000000000000) is 0
Math.round(0.4999999999999999167332731531132594682276248931884765625000000000000000000000000000000000000000000001) is 1

In Java 7 prints
Math.round(0.4999999999999999722444243843710864894092082977294921874999999999999999999999999999999999999999999999) is 0
Math.round(0.4999999999999999722444243843710864894092082977294921875000000000000000000000000000000000000000000000) is 1

 */

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SmallestNumberToRoundToOne {

    public static final BigDecimal TWO = BigDecimal.valueOf(2);

    public static void main(String... args) {
        int digits = 80;

        BigDecimal low = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ONE;

        for (int i = 0; i <= 10 * digits / 3; i++) {
            BigDecimal mid = low.add(high).divide(TWO, digits, RoundingMode.HALF_UP);
            if (mid.equals(low) || mid.equals(high))
                break;
            if (Math.round(Double.parseDouble(mid.toString())) > 0)
                high = mid;
            else
                low = mid;
        }

        System.out.println("Math.round(" + low + ") is " +
                Math.round(Double.parseDouble(low.toString())));
        System.out.println("Math.round(" + high + ") is " +
                Math.round(Double.parseDouble(high.toString())));
    }

    static class TwoWaysToConvertDoubleToBigDecimal {
        public static void main(String... args) {
            System.out.println("new BigDecimal(0.1)= " + new BigDecimal(0.1));
            System.out.println("BigDecimal.valueOf(0.1)= " + BigDecimal.valueOf(0.1));
        }
    }

    static class CalculateRepresentedValuesAndMidPoints {
        public static void main(String... args) {
            long value = Double.doubleToLongBits(0.5);
            final BigDecimal x0 = new BigDecimal(0.5);
            System.out.println("Value 0.5 is " + x0);
            final BigDecimal x1 = new BigDecimal(Double.longBitsToDouble(value - 1));
            System.out.println("The previous value is " + x1);
            final BigDecimal x2 = new BigDecimal(Double.longBitsToDouble(value - 2));
            System.out.println("... and the previous is " + x2);
            final BigDecimal two = BigDecimal.valueOf(2);
            System.out.println("\nThe mid point between " + x0 + "\n\tand " + x1 + "\n\tis " + x0.add(x1).divide(two));
            System.out.println("\n... and the mid point between " + x1 + "\n\tand " + x2 + "\n\tis " + x1.add(x2).divide(two));
        }
    }
}
