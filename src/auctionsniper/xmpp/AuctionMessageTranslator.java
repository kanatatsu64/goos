package auctionsniper.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {
  private final String sniperId;
  private final AuctionEventListener listener;

  public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
    this.sniperId = sniperId;
    this.listener = listener;
  }

  public void processMessage(Chat chat, Message message) {
    try {
      translate(message.getBody());
    } catch (Exception parseException) {
      listener.auctionFailed();
    }
  }

  private void translate(String messageBody) throws Exception {
    AuctionEvent event = AuctionEvent.from(messageBody);

    String type = event.type();
    if ("CLOSE".equals(type)) {
      listener.auctionClosed();
    } else if ("PRICE".equals(type)) {
      listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
    }
  }

  private static class AuctionEvent {
    private class MissingValueException extends Exception {
      public MissingValueException(String name) {
        super(name);
      }
    }

    private final Map<String, String> fields = new HashMap<String, String>();

    public String type() throws Exception {
      return get("Event");
    }

    public int currentPrice() throws Exception {
      return getInt("CurrentPrice");
    }

    public int increment() throws Exception {
      return getInt("Increment");
    }

    public PriceSource isFrom(String sniperId) throws Exception {
      return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
    }

    private int getInt(String fieldName) throws Exception {
      return Integer.parseInt(get(fieldName));
    }

    private String get(String fieldName) throws Exception {
      String value = fields.get(fieldName);
      if (null == value) {
        throw new MissingValueException(fieldName);
      }
      return value;
    }

    private void addField(String field) {
      String[] pair = field.split(":");
      fields.put(pair[0].trim(), pair[1].trim());
    }

    private String bidder() throws Exception {
      return get("Bidder");
    }

    static AuctionEvent from(String messageBody) {
      AuctionEvent event = new AuctionEvent();
      for (String field : fieldsIn(messageBody)) {
        event.addField(field);
      }
      return event;
    }

    static String[] fieldsIn(String messageBody) {
      return messageBody.split(";");
    }
  }
}
