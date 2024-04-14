package com.rodion.adelie.pfm.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record TradeEntry(Date date, String ticker, int qty, float price, Side side) {
    /**
     * Create access list entry.
     *
     * @param date trade date
     * @param ticker ticker name
     * @param qty quantity transacted
     * @param price price transacted at
     * @param side buy or sell
     * @return the trade entry
     */
    @JsonCreator
    public static TradeEntry createTradeEntry(
            @JsonProperty("date") final Date date,
            @JsonProperty("ticker") final String ticker,
            @JsonProperty("qty") final int qty,
            @JsonProperty("price") final float price,
            @JsonProperty("side") final Side side) {
        return new TradeEntry(
                date, ticker, qty, price, side);
    }

    public enum Side {
        buy,
        sell
    }
}
