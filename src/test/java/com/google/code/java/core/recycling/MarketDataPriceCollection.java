/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package com.google.code.java.core.recycling;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MarketDataPriceCollection {
    private final List<MarketDataPrice> marketDataPrices = new ArrayList<MarketDataPrice>();

    public MarketDataPriceCollection() {
    }

    public MarketDataPrice get(int symbol) {
        while (symbol >= marketDataPrices.size())
            marketDataPrices.add(new MarketDataPrice(marketDataPrices.size()));
        return marketDataPrices.get(symbol);
    }

    public void set(int symbol, MarketDataPrice marketDataPrice) {
        while (symbol >= marketDataPrices.size()) marketDataPrices.add(null);
        marketDataPrices.set(symbol, marketDataPrice);
    }

    public void writeTo(ByteBuffer bb) {
        bb.putInt(marketDataPrices.size());
        for (int i = 0; i < marketDataPrices.size(); i++) {
            MarketDataPrice mdp = marketDataPrices.get(i);
            bb.putInt(mdp.symbol);
            mdp.writeTo(bb);
        }
    }

    public void readFrom(ByteBuffer bb) {
        int length = bb.getInt();
        while (marketDataPrices.size() > length)
            marketDataPrices.remove(marketDataPrices.size() - 1); // remove last.
        for (int i = 0; i < length; i++) {
            get(i).readFrom(bb);
        }
    }

    public static MarketDataPriceCollection loadFrom(ByteBuffer bb) {
        MarketDataPriceCollection mdpc = new MarketDataPriceCollection();
        mdpc.readFrom(bb);
        return mdpc;
    }
}
