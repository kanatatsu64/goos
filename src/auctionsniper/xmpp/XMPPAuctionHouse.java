package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionHouse;

public class XMPPAuctionHouse implements AuctionHouse {
  private static final String AUCTION_RESOURCE = "Auction";
  private final XMPPConnection connection;

  private XMPPAuctionHouse(XMPPConnection connection) {
    this.connection = connection;
  }

  public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
    XMPPConnection connection = connection(hostname, username, password);
    return new XMPPAuctionHouse(connection);
  }

  public Auction auctionFor(String itemId) {
    return new XMPPAuction(connection, itemId);
  }

  public void disconnect() {
    connection.disconnect();
  }

  private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
    XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);

    return connection;
  }
}
