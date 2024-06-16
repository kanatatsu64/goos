package auctionsniper;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.SniperListener;
import auctionsniper.ui.Announcer;

public class AuctionSniper implements AuctionEventListener {
  private final Announcer<SniperListener> sniperListeners = new Announcer<>(SniperListener.class);
  private final Auction auction;
  private SniperSnapshot snapshot;
  private Item item;

  public AuctionSniper(Auction auction, SniperSnapshot snapshot, Item item) {
    this.auction = auction;
    this.snapshot = snapshot;
    this.item = item;
  }

  public void auctionClosed() {
    snapshot = snapshot.close();
    notifyChange();
  }

  public void auctionFailed() {
    // TODO
  }

  public void currentPrice(int price, int increment, PriceSource priceSource) {
    switch (priceSource) {
      case FromSniper:
        snapshot = snapshot.winning(price);
        break;
      case FromOtherBidder:
        int bid = price + increment;
        if (item.allowsBid(bid)) {
          auction.bid(bid);
          snapshot = snapshot.bidding(price, bid);
        } else {
          snapshot = snapshot.losing(price);
        }
    }
    notifyChange();
  }

  public void addSniperListener(SniperListener listener) {
    sniperListeners.addListener(listener);
  }

  public SniperSnapshot getSnapshot() {
    return snapshot;
  }

  private void notifyChange() {
    sniperListeners.announce().sniperStateChanged(snapshot);
  }
}
