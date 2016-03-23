package com.smartdsepro.android;

/**
 * Created by mashnoor on 1/18/16.
 */
public class MarketDepthValue {
    String price;
    String volume;

    public String getPrice() {
        return price;
    }

    public String getVolume() {
        return volume;
    }

    public MarketDepthValue(String price, String volume) {

        this.price = price;
        this.volume = volume;
    }
}
