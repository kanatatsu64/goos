package test.integration;

import org.junit.Test;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import auctionsniper.SniperPortfolio;
import auctionsniper.interfaces.UserRequestListener;
import auctionsniper.ui.MainWindow;

import test.AuctionSniperDriver;

public class MainWindowTest {
  private final SniperPortfolio portfolio = new SniperPortfolio();
  private final MainWindow mainWindow = new MainWindow(portfolio);
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
