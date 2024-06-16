package auctionsniper.xmpp;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.FailureReporter;
import auctionsniper.ui.Announcer;

public class XMPPAuction implements Auction {
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

  private static final String AUCTION_RESOURCE = "Auction";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

  private final Chat chat;
  private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

  public XMPPAuction(XMPPConnection connection, String itemId, FailureReporter failureReporter) {
    AuctionMessageTranslator translator = translatorFor(connection, failureReporter);
    this.chat = connection.getChatManager().createChat(auctionId(itemId, connection), translator);
    addAuctionEventListener(chatDisconnectorFor(translator));
  }

  private static String auctionId(String itemId, XMPPConnection connection) {
    return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
  }

  public void addAuctionEventListener(AuctionEventListener listener) {
    auctionEventListeners.addListener(listener);
  }

  public void bid(int amount) {
    sendMessage(format(BID_COMMAND_FORMAT, amount));
  }

  public void join() {
    sendMessage(JOIN_COMMAND_FORMAT);
  }

  private AuctionMessageTranslator translatorFor(XMPPConnection connection, FailureReporter failureReporter) {
    return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
  }

  private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
    return new AuctionEventListener() {
      @Override
      public void auctionFailed() {
        chat.removeMessageListener(translator);
      }

      @Override
      public void auctionClosed() {
      }

      @Override
      public void currentPrice(int price, int increment, PriceSource source) {
      }
    };
  }

  private void sendMessage(String message) {
    try {
      chat.sendMessage(message);
    } catch (XMPPException e) {
      e.printStackTrace();
    }
  }
}
