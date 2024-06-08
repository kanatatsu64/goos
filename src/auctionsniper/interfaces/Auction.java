package auctionsniper.interfaces;

public interface Auction {
  void addAuctionEventListener(AuctionEventListener listener);

  void bid(int amount);

  void join();
}
