package test.integration;

import org.junit.Test;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import auctionsniper.Item;
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
    final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<Item>(
        equalTo(new Item("an item-id", 1000)),
        "join request");

    mainWindow.addUserRequestListener(
        new UserRequestListener() {
          public void joinAuction(Item item) {
            itemProbe.setReceivedValue(item);
          }
        });

    driver.startBiddingFor("an item-id", 1000);
    driver.check(itemProbe);
  }
}
