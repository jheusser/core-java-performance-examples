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

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SmallestNumberToRoundToOne {
    public static void main(String... args) {
        int digits = 100;

        BigDecimal low = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ONE;

        for (int i = 0; i <= 10 * digits / 3; i++) {
            BigDecimal mid = low.add(high).divide(BigDecimal.valueOf(2), digits, RoundingMode.HALF_UP);
            if (mid.equals(low) || mid.equals(high))
                break;
            if (Math.round(Double.parseDouble(mid.toString())) > 0)
                high = mid;
            else
                low = mid;
        }

        System.out.println("Math.round(" + low + ") is " + Math.round(Double.parseDouble(low.toString())));
        System.out.println("Math.round(" + high + ") is " + Math.round(Double.parseDouble(high.toString())));
    }
}
