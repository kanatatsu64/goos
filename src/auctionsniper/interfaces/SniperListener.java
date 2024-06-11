package auctionsniper.interfaces;

import java.util.EventListener;

import auctionsniper.SniperSnapshot;

public interface SniperListener extends EventListener {
  void sniperStateChanged(SniperSnapshot sniperSnapshot);

  void sniperAdded(SniperSnapshot sniperSnapshot);
}
