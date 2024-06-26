package test.integration;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuctionHouse;
import test.ApplicationRunner;
import test.FakeAuctionServer;

public class XMPPAuctionTest {
  private final FakeAuctionServer server = new FakeAuctionServer("item-54321");

  @Test
  public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
    XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
        FakeAuctionServer.XMPP_HOSTNAME,
        ApplicationRunner.SNIPER_ID,
        ApplicationRunner.SNIPER_PASSWORD);
    CountDownLatch auctionWasClosed = new CountDownLatch(1);

    server.startSellingItem();

    Auction auction = auctionHouse.auctionFor(server.getItemId());
    auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

    auction.join();
    server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    server.announceClosed();

    assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
  }

  private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
    return new AuctionEventListener() {
      @Override
      public void auctionClosed() {
        auctionWasClosed.countDown();
      }

      @Override
      public void auctionFailed() {
        // TODO

      }

      @Override
      public void currentPrice(int price, int increment, PriceSource priceSource) {
        // TODO
      }
    };
  }
}
