package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.SniperListener;
import auctionsniper.interfaces.AuctionEventListener.PriceSource;

public class AcutinoSniperTest {
  private final String ITEM_ID = "item id";

  private final Mockery context = new Mockery();
  private final States sniperState = context.states("sniper");

  private final SniperListener sniperListener = context.mock(SniperListener.class);
  private final Auction auction = context.mock(Auction.class);
  private final SniperSnapshot snapshot = SniperSnapshot.joining(ITEM_ID);
  private final AuctionSniper sniper = new AuctionSniper(auction, snapshot);

  @Before
  public void addSniperListener() {
    sniper.addSniperListener(sniperListener);
  }

  @Test
  public void reportsLostIfAuctionClosesImmediately() {
    context.checking(new Expectations() {
      {
        atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
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

        atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
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

        atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));
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
        atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
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
