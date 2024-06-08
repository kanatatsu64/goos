package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.interfaces.AuctionEventListener;
import auctionsniper.interfaces.SniperListener;
import auctionsniper.interfaces.UserRequestListener;
import auctionsniper.ui.Announcer;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.SwingThreadSniperListener;
import auctionsniper.xmpp.XMPPAuction;

public class Main {
  private final SnipersTableModel snipers = new SnipersTableModel();
  private MainWindow ui;
  @SuppressWarnings("unused")
  private ArrayList<XMPPAuction> notToBeGCd = new ArrayList<XMPPAuction>();

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;

  private static final String AUCTION_RESOURCE = "Auction";

  public Main() throws Exception {
    startUserInterface();
  }

  public static void main(String... args) throws Exception {
    Main main = new Main();
    XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
    main.disconnectWhenUICloses(connection);
    main.addUserRequestListenerFor(connection);
  }

  private void addUserRequestListenerFor(final XMPPConnection connection) {
    ui.addUserRequestListener(new UserRequestListener() {
      public void joinAuction(String itemId) {
        SniperSnapshot snapshot = SniperSnapshot.joining(itemId);
        snipers.addSniper(snapshot);

        XMPPAuction auction = new XMPPAuction(connection, itemId);
        notToBeGCd.add(auction);

        SniperListener sniperListener = new SwingThreadSniperListener(snipers);
        AuctionSniper sniper = new AuctionSniper(auction, sniperListener, snapshot);
        auction.addAuctionEventListener(sniper);

        auction.join();
      }
    });
  }

  private void disconnectWhenUICloses(final XMPPConnection connection) {
    ui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        connection.disconnect();
      }
    });
  }

  private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
    XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);

    return connection;
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        ui = new MainWindow(snipers);
      }
    });
  }
}
