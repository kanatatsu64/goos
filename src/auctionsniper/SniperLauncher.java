package auctionsniper;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionHouse;
import auctionsniper.interfaces.SniperCollector;
import auctionsniper.interfaces.UserRequestListener;

public class SniperLauncher implements UserRequestListener {
  private final SniperCollector collector;
  private AuctionHouse auctionHouse;

  public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
    this.collector = collector;
    this.auctionHouse = auctionHouse;
  }

  public void joinAuction(String itemId) {
    Auction auction = auctionHouse.auctionFor(itemId);
    SniperSnapshot snapshot = SniperSnapshot.joining(itemId);
    AuctionSniper sniper = new AuctionSniper(auction, snapshot);
    collector.addSniper(sniper);

    auction.addAuctionEventListener(sniper);
    auction.join();
  }
}
