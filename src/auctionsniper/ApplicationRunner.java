package auctionsniper;

public class ApplicationRunner {
  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_XMPP_ID = "sniper@98ee5241f8d0/Auction";

  private String itemId;
  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer auction) {
    itemId = auction.getItemId();

    Thread thread = new Thread("Test Application") {
      @Override
      public void run() {
        try {
          Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    driver = new AuctionSniperDriver(3000);
    driver.showsSniperStatus(MainWindow.STATUS_JOINING);
  }

  public void showsSniperHasLostAuction(int lastPrice) {
    driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_LOST);
  }

  public void showsSniperHasWonAuction(int lastPrice) {
    driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
  }

  public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
    driver.showsSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING);
  }

  public void hasShownSniperIsWinning(int winningBid) {
    driver.showsSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
  }

  public void stop() {
    if (driver != null) {
      driver.dispose();
    }
  }
}
