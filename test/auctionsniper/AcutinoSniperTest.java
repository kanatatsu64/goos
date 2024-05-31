package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.AuctionEventListener.PriceSource;

public class AcutinoSniperTest {
  private final String ITEM_ID = "item id";

  private final Mockery context = new Mockery();
  private final States sniperState = context.states("sniper");

  private final SniperListener sniperListener = context.mock(SniperListener.class);
  private final Auction auction = context.mock(Auction.class);
  private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);

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

        allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
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
        allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
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
    final int bid = price + increment;

    context.checking(new Expectations() {
      {
        oneOf(auction).bid(bid);
        atLeast(1).of(sniperListener).sniperStateChanged(
            new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
      }
    });

    sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

    context.assertIsSatisfied();
  }

  @Test
  public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    context.checking(new Expectations() {
      {
        ignoring(auction);

        allowing(sniperListener).sniperStateChanged(
            with(aSniperThatIs(SniperState.BIDDING)));
        then(sniperState.is("bidding"));

        atLeast(1).of(sniperListener).sniperStateChanged(
            new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
        when(sniperState.is("bidding"));
      }
    });

    sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
    sniper.currentPrice(135, 45, PriceSource.FromSniper);

    context.assertIsSatisfied();
  }

  private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
    return new FeatureMatcher<SniperSnapshot, SniperState>(
        equalTo(state), "sniper that is ", "was") {
      @Override
      protected SniperState featureValueOf(SniperSnapshot actual) {
        return actual.state;
      }
    };
  }
}
