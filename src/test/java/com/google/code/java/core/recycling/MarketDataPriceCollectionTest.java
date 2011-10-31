/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.recycling;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MarketDataPriceCollectionTest {

    public static final int COPY_SIZE = 2 * 1024;
    public static final long[] bytes1 = new long[COPY_SIZE];
    public static final long[] bytes2 = new long[COPY_SIZE];

    final int prices = 1000;
    final int testCount = 1000000;
    final int runs = 10;
    final int warmup = 10000 / runs;

    @Test
    public void testPerf() {
        MarketDataPriceCollection mdpc = new MarketDataPriceCollection();
        for (int i = 0; i < prices; i++) {
            final MarketDataPrice mdp = mdpc.get(i);
            mdp.bestBid = 0.998;
            mdp.bestOffer = 1.002;
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(1024 * 1024);
        mdpc.writeTo(bb);
        bb.flip();
        StringBuilder results = new StringBuilder();

        int[] recycleTime = new int[testCount], recycleCache = new int[testCount], newObjectTime = new int[testCount], newObjectCache = new int[testCount];
        final MarketDataPriceCollection recycled = new MarketDataPriceCollection();
        for (int i = -warmup; i < testCount; i++) {
            doRecycleDeserialize(bb, i, recycleTime, recycleCache, recycled);
//            doNewObjectDeserialize(bb, i, newObjectTime, newObjectCache);
        }
        Arrays.sort(recycleTime);
        Arrays.sort(recycleCache);
        Arrays.sort(newObjectTime);
        Arrays.sort(newObjectCache);
        System.out.println("\npercentile, recycled deserialize,recycle copy latency,new object deserialize,new object copy latency");
        for (int p = 1; p <= 99; p += 2) {
            int index = testCount * p / 100;
            System.out.printf("%2d%%,%d,%d,%d,%d%n", p, recycleTime[index], recycleCache[index], newObjectTime[index], newObjectCache[index]);
        }
        System.out.println("\npercentile, recycled deserialize,recycle copy latency,new object deserialize,new object copy latency");
        for (int p = 951; p <= 999; p += 2) {
            int index = testCount * p / 1000;
            System.out.printf("%4.1f%%,%d,%d,%d,%d%n", p / 10.0, recycleTime[index], recycleCache[index], newObjectTime[index], newObjectCache[index]);
        }
        System.out.println(results);
    }

    private void doRecycleDeserialize(ByteBuffer bb, int count, int[] recycleTime, int[] recycleCache, MarketDataPriceCollection mdpc) {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            bb.position(0);
            mdpc.readFrom(bb);
        }
        final long mid = System.nanoTime();
        long time = mid - start;
        System.arraycopy(bytes1, 0, bytes2, 0, bytes1.length);
        long copyTime = System.nanoTime() - mid;
        if (count >= 0) {
            recycleTime[count] = (int) (time / runs);
            recycleCache[count] = (int) copyTime;
        }
    }

    private void doNewObjectDeserialize(ByteBuffer bb, int count, int[] newObjectTime, int[] newObjectCache) {
        MarketDataPriceCollection mdpc = null;
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            bb.position(0);
            mdpc = MarketDataPriceCollection.loadFrom(bb);
        }
        final long mid = System.nanoTime();
        long time = mid - start;
        System.arraycopy(bytes1, 0, bytes2, 0, bytes1.length);
        long copyTime = System.nanoTime() - mid;
        if (count >= 0) {
            newObjectTime[count] = (int) (time / runs);
            newObjectCache[count] = (int) copyTime;
        }
    }
}
