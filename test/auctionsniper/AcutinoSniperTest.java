package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.AuctionEventListener.PriceSource;

public class AcutinoSniperTest {
  private final Mockery context = new Mockery();
  private final States sniperState = context.states("sniper");

  private final SniperListener sniperListener = context.mock(SniperListener.class);
  private final Auction auction = context.mock(Auction.class);
  private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

  @Test
  public void reportsLostIfAuctionClosesImmediately() {
    context.checking(new Expectations() {
      {
        atLeast(1).of(sniperListener).sniperLost();
      }
    });

    sniper.auctionClosed();

    context.assertIsSatisfied();
  }

  @Test
  public void reportsLostIfAuctionClosesWhenBidding() {
    context.checking(new Expectations() {
      {
        ignoring(auction);

        allowing(sniperListener).sniperBidding();
        then(sniperState.is("bidding"));

        atLeast(1).of(sniperListener).sniperLost();
        when(sniperState.is("bidding"));
      }
    });

    sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    sniper.auctionClosed();
  }

  @Test
  public void reportsWonIfAuctionClosesWhenWinning() {
    context.checking(new Expectations() {
      {
        ignoring(auction);
        allowing(sniperListener).sniperWinning();
        then(sniperState.is("winning"));

        atLeast(1).of(sniperListener).sniperWon();
        when(sniperState.is("winning"));
      }
    });

    sniper.currentPrice(123, 45, PriceSource.FromSniper);
    sniper.auctionClosed();
  }

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

  @Test
  public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
    final int price = 1001;
    final int increment = 25;
    context.checking(new Expectations() {
      {
        oneOf(auction).bid(price + increment);
        atLeast(1).of(sniperListener).sniperBidding();
      }
    });

    sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

    context.assertIsSatisfied();
  }

  @Test
  public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    context.checking(new Expectations() {
      {
        atLeast(1).of(sniperListener).sniperWinning();
      }
    });

    sniper.currentPrice(123, 45, PriceSource.FromSniper);

    context.assertIsSatisfied();
  }
}
