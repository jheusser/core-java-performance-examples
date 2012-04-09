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

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HiresTimerTest {
    @Test
    public void testCurrentTimeUS() throws Exception {
        int count = 0;
        for (int i = 0; i < 100; i++) {
            final long timeUS = HiresTimer.currentTimeUS();
            System.out.println(HiresTimer.toString(timeUS));
            if (timeUS % 1000 == 0)
                count++;
        }
        assertEquals(1, count, 1);
    }

    @Test
    public void testToString() throws Exception {
        String[] expected = ("1970/01/01T00:00:00.000001\n" +
                "1970/01/01T00:00:00.000010\n" +
                "1970/01/01T00:00:00.000100\n" +
                "1970/01/01T00:00:00.001000\n" +
                "1970/01/01T00:00:00.010000\n" +
                "1970/01/01T00:00:00.100000\n" +
                "1970/01/01T00:00:01.000000\n" +
                "1970/01/01T00:00:10.000000\n" +
                "1970/01/01T00:01:40.000000\n" +
                "1970/01/01T00:16:40.000000\n" +
                "1970/01/01T02:46:40.000000\n" +
                "1970/01/02T03:46:40.000000\n" +
                "1970/01/12T13:46:40.000000\n" +
                "1970/04/26T17:46:40.000000\n" +
                "1973/03/03T09:46:40.000000\n" +
                "2001/09/09T01:46:40.000000\n" +
                "2286/11/20T17:46:40.000000").split("\n");
        int n = 0;
        for (long i = 1; i < 1e17; i *= 10)
            assertEquals(expected[n++], HiresTimer.toString(i));
    }
}
