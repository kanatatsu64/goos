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

  public void joinAuction(Item item) {
    Auction auction = auctionHouse.auctionFor(item.identifier);
    SniperSnapshot snapshot = SniperSnapshot.joining(item.identifier);
    AuctionSniper sniper = new AuctionSniper(auction, snapshot, item);
    collector.addSniper(sniper);

    auction.addAuctionEventListener(sniper);
    auction.join();
  }
}
