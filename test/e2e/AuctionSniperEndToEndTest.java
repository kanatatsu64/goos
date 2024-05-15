package test.e2e;

import org.junit.After;
import org.junit.Test;
import auctionsniper.FakeAuctionServer;
import auctionsniper.ApplicationRunner;

public class AuctionSniperEndToEndTest {
  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUnitlAuctionCloses() throws Exception {
    auction.startSellingItem();
    application.startBiddingIn(auction);
    auction.hasReceivedJoinRequestFromSniper();
    auction.announceClosed();
    application.showsSniperHasLostAuction();
  }

  @After
  public void stopAuction() {
    auction.stop();
  }

  @After
  public void stopApplication() {
    application.stop();
  }
}
