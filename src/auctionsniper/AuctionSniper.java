package auctionsniper;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.SniperListener;
import auctionsniper.ui.Announcer;

public class AuctionSniper implements AuctionEventListener {
  private final Announcer<SniperListener> sniperListeners = new Announcer<>(SniperListener.class);
  private final Auction auction;
  private SniperSnapshot snapshot;

  public AuctionSniper(Auction auction, SniperSnapshot snapshot) {
    this.auction = auction;
    this.snapshot = snapshot;
  }

  public void auctionClosed() {
    snapshot = snapshot.close();
    notifyChange();
  }

  public void currentPrice(int price, int increment, PriceSource priceSource) {
    switch (priceSource) {
      case FromSniper:
        snapshot = snapshot.winning(price);
        break;
      case FromOtherBidder:
        int bid = price + increment;
        auction.bid(bid);
        snapshot = snapshot.bidding(price, bid);
    }
    notifyChange();
  }

  public void addSniperListener(SniperListener listener) {
    listener.sniperAdded(snapshot);
    sniperListeners.addListener(listener);
  }

  private void notifyChange() {
    sniperListeners.announce().sniperStateChanged(snapshot);
  }
}
