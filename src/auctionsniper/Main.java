package auctionsniper;

import static java.lang.String.format;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {
  private MainWindow ui;
  @SuppressWarnings("unused")
  private Chat notToBeGCd;

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;
  private static final int ARG_ITEM_ID = 3;

  private static final String AUCTION_RESOURCE = "Auction";
  public static final String ITEM_ID_AS_LOGIN = "auction-%s";
  public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

  public Main() throws Exception {
    startUserInterface();
  }

  public static void main(String... args) throws Exception {
    Main main = new Main();
    main.joinAuction(
        connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
        args[ARG_ITEM_ID]);

    System.out.println("Press any key to exit");
  }

  private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    class XMPPAuction implements Auction {
      private final Chat chat;

      public XMPPAuction(Chat chat) {
        this.chat = chat;
      }

      public void bid(int amount) {
        sendMessage(format(BID_COMMAND_FORMAT, amount));
      }

      public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
      }

      private void sendMessage(String message) {
        try {
          chat.sendMessage(message);
        } catch (XMPPException e) {
          e.printStackTrace();
        }
      }
    }

    class SniperStateDisplayer implements SniperListener {
      private MainWindow ui;

      public SniperStateDisplayer(MainWindow ui) {
        this.ui = ui;
      }

      public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ui.sniperStateChanged(sniperSnapshot);
          }
        });
      }
    }

    disconnectWhenUICloses(connection);

    final Chat chat = connection.getChatManager().createChat(
        auctionId(itemId, connection),
        null);
    this.notToBeGCd = chat;

    XMPPAuction auction = new XMPPAuction(chat);
    SniperListener sniperListener = new SniperStateDisplayer(ui);
    AuctionEventListener sniper = new AuctionSniper(auction, sniperListener, itemId);
    MessageListener messageListener = new AuctionMessageTranslator(connection.getUser(), sniper);

    chat.addMessageListener(messageListener);

    auction.join();
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

  private static String auctionId(String itemId, XMPPConnection connection) {
    return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        ui = new MainWindow();
      }
    });
  }
}
