package test;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {
  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_XMPP_ID = "sniper@98ee5241f8d0/Auction";

  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer... auctions) {
    startSniper();

    for (FakeAuctionServer auction : auctions) {
      String itemId = auction.getItemId();
      driver.startBiddingFor(itemId, Integer.MAX_VALUE);
      driver.showsSniperStatus(auction.getItemId(), 0, 0, "Joining");
    }
  }

  public void startBiddingWithStopPrice(final int stopPrice, final FakeAuctionServer... auctions) {
    startSniper();

    for (FakeAuctionServer auction : auctions) {
      String itemId = auction.getItemId();
      driver.startBiddingFor(itemId, stopPrice);
      driver.showsSniperStatus(auction.getItemId(), 0, 0, "Joining");
    }
  }

  public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
    driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, "Lost");
  }

  public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
    driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, "Won");
  }

  public void showsSniperHasFailed(FakeAuctionServer auction) {
    driver.showsSniperStatus(auction.getItemId(), 0, 0, "Failed");
  }

  public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
    driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, "Bidding");
  }

  public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
    driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, "Losing");
  }

  public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
    driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, "Winning");
  }

  public void reportsInvalidMessage(FakeAuctionServer auction, String invalidMessage) {
    // TODO
  }

  public void stop() {
    if (driver != null) {
      driver.dispose();
    }
  }

  private void startSniper() {
    Thread thread = new Thread("Test Application") {
      @Override
      public void run() {
        try {
          Main.main(arguments());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    driver = new AuctionSniperDriver(3000);
    driver.hasTitle(MainWindow.APPLICATION_TITLE);
    driver.hasColumnTitles();
  }

  private static String[] arguments(FakeAuctionServer... auctions) {
    String[] arguments = new String[auctions.length + 3];
    arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
    arguments[1] = SNIPER_ID;
    arguments[2] = SNIPER_PASSWORD;
    for (int i = 0; i < auctions.length; i++) {
      arguments[i + 3] = auctions[i].getItemId();
    }

    return arguments;
  }
}
