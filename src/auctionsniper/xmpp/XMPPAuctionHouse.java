package auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static java.nio.file.Paths.get;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.interfaces.Auction;
import auctionsniper.interfaces.AuctionHouse;
import auctionsniper.interfaces.FailureReporter;

public class XMPPAuctionHouse implements AuctionHouse {
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String LOG_FILE_NAME = "auction-sniper.log";
  private static final String LOGGER_NAME = "auction sniper logger";

  private final XMPPConnection connection;
  private final FailureReporter failureReporter;

  private XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException {
    this.connection = connection;
    this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
  }

  public static XMPPAuctionHouse connect(String hostname, String username, String password)
      throws XMPPException, XMPPAuctionException {
    XMPPConnection connection = connection(hostname, username, password);
    return new XMPPAuctionHouse(connection);
  }

  public Auction auctionFor(String itemId) {
    return new XMPPAuction(connection, itemId, failureReporter);
  }

  private Logger makeLogger() throws XMPPAuctionException {
    Logger logger = Logger.getLogger(LOGGER_NAME);
    logger.setUseParentHandlers(false);
    logger.addHandler(simpleFilehandler());
    return logger;
  }

  private FileHandler simpleFilehandler() throws XMPPAuctionException {
    try {
      FileHandler handler = new FileHandler(LOG_FILE_NAME);
      handler.setFormatter(new SimpleFormatter());
      return handler;
    } catch (Exception e) {
      throw new XMPPAuctionException("Could not create logger FileHandler "
          + get(LOG_FILE_NAME), e);
    }
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
