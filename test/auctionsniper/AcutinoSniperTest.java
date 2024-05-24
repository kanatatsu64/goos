package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

public class AcutinoSniperTest {
  private final Mockery context = new Mockery();
  private final SniperListener sniperListener = context.mock(SniperListener.class);
  private final AuctionSniper sniper = new AuctionSniper(sniperListener);

  @Test
  public void reportsLostWhenAuctionCloses() {
    context.checking(new Expectations() {
      {
        oneOf(sniperListener).sniperLost();
      }
    });

    sniper.auctionClosed();

    context.assertIsSatisfied();
  }
}
