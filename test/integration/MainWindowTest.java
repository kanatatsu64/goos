package test.integration;

import org.junit.Test;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import javax.swing.SwingUtilities;

import auctionsniper.interfaces.UserRequestListener;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import test.AuctionSniperDriver;

public class MainWindowTest {
  private final SnipersTableModel tableModel = new SnipersTableModel();
  private final MainWindow mainWindow = new MainWindow(tableModel);
  private final AuctionSniperDriver driver = new AuctionSniperDriver(1000);

  @Test
  public void makesUserRequestWhenJoinButtonClicked() throws Exception {
    final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");

    mainWindow.addUserRequestListener(
        new UserRequestListener() {
          public void joinAuction(String itemId) {
            buttonProbe.setReceivedValue(itemId);
          }
        });

    driver.startBiddingFor("an item-id");
    driver.check(buttonProbe);
  }
}
