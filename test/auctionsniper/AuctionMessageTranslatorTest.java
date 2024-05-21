package test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;

public class AuctionMessageTranslatorTest {
  public static final Chat UNUSED_CHAT = null;
  private final Mockery context = new Mockery();
  private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

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
  public void notifiesBidDetailsWhenCurrentPriceMessageReceived() {
    context.checking(new Expectations() {
      {
        oneOf(listener).currentPrice(192, 7);
      }
    });

    Message message = new Message();
    message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

    translator.processMessage(UNUSED_CHAT, message);

    context.assertIsSatisfied();
  }
}
