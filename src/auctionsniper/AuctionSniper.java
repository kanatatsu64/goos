package auctionsniper;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.SniperListener;

public class AuctionSniper implements AuctionEventListener {
  private final SniperListener sniperListener;
  private final Auction auction;
  private SniperSnapshot snapshot;

  public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
    this.auction = auction;
    this.sniperListener = sniperListener;
    this.snapshot = SniperSnapshot.joining(itemId);
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

  private void notifyChange() {
    sniperListener.sniperStateChanged(snapshot);
  }
}
