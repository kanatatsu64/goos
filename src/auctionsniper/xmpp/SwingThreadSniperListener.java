package auctionsniper.xmpp;

import javax.swing.SwingUtilities;

import auctionsniper.SniperSnapshot;
import auctionsniper.interfaces.SniperListener;

public class SwingThreadSniperListener implements SniperListener {
  private SniperListener listener;

  public SwingThreadSniperListener(SniperListener listener) {
    this.listener = listener;
  }

  public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        listener.sniperStateChanged(sniperSnapshot);
      }
    });
  }

  public void sniperAdded(SniperSnapshot sniperSnapshot) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        listener.sniperAdded(sniperSnapshot);
      }
    });
  }
}
