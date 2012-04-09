package com.google.code.java.core.time;/*
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public enum HiresTimer {
    ;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS");
    private static final DecimalFormat DF = new DecimalFormat("000");
    private static long s_deltaUS;

    static {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
        long now, nowNs, now0 = System.currentTimeMillis();
        // wait for the milli-seconds to change.
        do {
            nowNs = System.nanoTime();
        } while ((now = System.currentTimeMillis()) == now0);
        s_deltaUS = now * 1000 - (nowNs + 500) / 1000;
    }

    public static long currentTimeUS() {
        long now = System.currentTimeMillis() * 1000;
        long nowUS = (System.nanoTime() + 500) / 1000 + s_deltaUS;
        if (nowUS < now) {
            s_deltaUS++;
            return now;
        } else if (nowUS > now + 999) {
            s_deltaUS--;
            return now;
        }
        return nowUS;
    }

    public static String toString(long timeUS) {
        return SDF.format(timeUS / 1000) + DF.format(timeUS % 1000);
    }
}
