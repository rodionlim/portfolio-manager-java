package com.rodion.adelie.pfm.blotter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rodion.adelie.pfm.trade.TradeEntry;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBlotter implements Blotter {
  private static final Logger logger = LoggerFactory.getLogger(DefaultBlotter.class);

  private final BlotterStorage blotterStorage;

  private DefaultBlotter(BlotterStorage blotterStorage) {
    checkNotNull(blotterStorage);
    this.blotterStorage = blotterStorage;
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

  @Override
  public void awaitStop() throws InterruptedException {}

  @Override
  public List<TradeEntry> getTradesByTicker(String ticker) {
    return List.of();
  }

  @Override
  public int upsertTrade(TradeEntry trade) {
    return 0;
  }

  @Override
  public int removeTrade(int id) {
    return 0;
  }
}
