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

public class MarketDataPrice {
    final int symbol;
    double bestBid;
    double bestOffer;


    public MarketDataPrice(int symbol) {
        this.symbol = symbol;
    }


    public void readFrom(ByteBuffer bb) {
        // it is assumed the symbol has been read.
        bestBid = bb.getDouble();
        bestOffer = bb.getDouble();
    }

    public void writeTo(ByteBuffer bb) {
        // is is assumed the symbol will be written by the caller
        bb.putDouble(bestBid);
        bb.putDouble(bestOffer);
    }
}
