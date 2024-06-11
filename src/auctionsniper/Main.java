package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import auctionsniper.interfaces.AuctionHouse;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {
  private final SnipersTableModel snipers = new SnipersTableModel();
  private MainWindow ui;

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;

  public Main() throws Exception {
    startUserInterface();
  }

  public static void main(String... args) throws Exception {
    Main main = new Main();
    XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
        args[ARG_HOSTNAME],
        args[ARG_USERNAME],
        args[ARG_PASSWORD]);
    main.disconnectWhenUICloses(auctionHouse);
    main.addUserRequestListenerFor(auctionHouse);
  }

  private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
    SniperLauncher sniperLauncher = new SniperLauncher(snipers, auctionHouse);
    ui.addUserRequestListener(sniperLauncher);
  }

  private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
    ui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        auctionHouse.disconnect();
      }
    });
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        ui = new MainWindow(snipers);
      }
    });
  }
}
