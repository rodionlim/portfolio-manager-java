package com.rodion.adelie.pfm.blotter;

import com.rodion.adelie.pfm.trade.TradeEntry;

import java.util.List;

/** An interface for interacting with the blotter */
public interface Blotter {
    /**
     * Return the list of trade entries pertaining to a ticker
     *
     * @return List of trade entries
     */
    List<TradeEntry> getTradesByTicker(String ticker);

    /**
     * Upsert a trade to the blotter
     *
     * @param trade trade entry details
     * @return trade id of the newly added entry
     */
    int upsertTrade(TradeEntry trade);

    /**
     * Remove a trade from the blotter
     *
     * @param id trade id of the removed entry
     * @return trade id of the removed entry
     */
    int removeTrade(int id);
}
