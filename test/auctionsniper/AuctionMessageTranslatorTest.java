package test.auctionsniper;

import static test.ApplicationRunner.SNIPER_ID;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.FailureReporter;
import auctionsniper.interfaces.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.AuctionMessageTranslator;

public class AuctionMessageTranslatorTest {
  public static final Chat UNUSED_CHAT = null;
  private final Mockery context = new Mockery();
  private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
  private final FailureReporter failureReporter = context.mock(FailureReporter.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener,
      failureReporter);

  @Test
  public void notifiesAuctionClosedWhenCloseMessagereceived() {
    context.checking(new Expectations() {
      {
        oneOf(listener).auctionClosed();
      }
    });

    Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: CLOSE;");

    translator.processMessage(UNUSED_CHAT, message);

    context.assertIsSatisfied();
  }

  @Test
  public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
    context.checking(new Expectations() {
      {
        oneOf(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
      }
    });

    Message message = new Message();
    message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

    translator.processMessage(UNUSED_CHAT, message);

    context.assertIsSatisfied();
  }

  @Test
  public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
    context.checking(new Expectations() {
      {
        oneOf(listener).currentPrice(192, 7, PriceSource.FromSniper);
      }
    });

    Message message = new Message();
    message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: " + SNIPER_ID + ";");

    translator.processMessage(UNUSED_CHAT, message);

    context.assertIsSatisfied();
  }

  @Test
  public void notifiesAuctionFailedWhenBadMessageReceived() {
    context.checking(new Expectations() {
      {
        exactly(1).of(listener).auctionFailed();
      }
    });

    String badMessage = "a bad message";
    expectFailureWithMessage(badMessage);
    translator.processMessage(UNUSED_CHAT, message(badMessage));
  }

  @Test
  public void notifiesAuctionFailedWhenEventTypeMissing() {
    context.checking(new Expectations() {
      {
        exactly(1).of(listener).auctionFailed();
      }
    });

    Message message = new Message();
    message.setBody("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
    translator.processMessage(UNUSED_CHAT, message);
  }

  private Message message(String body) {
    Message message = new Message();
    message.setBody(body);
    return message;
  }

  private void expectFailureWithMessage(final String badMessage) {
    context.checking(new Expectations() {
      {
        oneOf(listener).auctionFailed();
        oneOf(failureReporter).cannotTranslateMessage(
            with(SNIPER_ID), with(badMessage),
            with(any(Exception.class)));
      }
    });
  }
}
