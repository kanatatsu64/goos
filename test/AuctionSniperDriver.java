package test;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.ComponentSelector;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import auctionsniper.ui.MainWindow;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static java.lang.String.valueOf;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

public class AuctionSniperDriver extends JFrameDriver {
  public AuctionSniperDriver(int timeoutMillis) {
    System.setProperty("com.objogate.wl.keyboard", "Mac-GB");
    super(new GesturePerformer(),
        JFrameDriver.topLevelFrame(
            named(MainWindow.MAIN_WINDOW_NAME),
            showingOnScreen()),
        new AWTEventQueueProber(timeoutMillis, 100));
  }

  public void startBiddingFor(String itemId, int stopPrice) {
    // itemIdField().replaceAllText(itemId);
    ComponentSelector<JTextField> itemIdFieldFinder = itemIdField().component();
    itemIdFieldFinder.probe();
    JTextField itemIdField = itemIdFieldFinder.component();
    itemIdField.setText(itemId);

    // itemStopPriceField().replaceAllText(stopPrice);
    ComponentSelector<JTextField> stopPriceFieldFinder = stopPriceField().component();
    stopPriceFieldFinder.probe();
    JTextField stopPriceField = stopPriceFieldFinder.component();
    stopPriceField.setText(String.valueOf(stopPrice));

    // bidButton().click();
    ComponentSelector<JButton> buttonFinder = bidButton().component();
    buttonFinder.probe();
    JButton button = buttonFinder.component();
    button.doClick();
  }

  private JTextFieldDriver itemIdField() {
    return new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
  }

  private JTextFieldDriver stopPriceField() {
    return new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_STOP_PRICE_NAME));
  }

  private JButtonDriver bidButton() {
    return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
  }

  public void showsSniperStatus(String statusText) {
    JTableDriver table = new JTableDriver(this);
    table.hasCell(withLabelText(statusText));
  }

  public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
    JTableDriver table = new JTableDriver(this);
    table.hasRow(
        matching(
            withLabelText(itemId),
            withLabelText(valueOf(lastPrice)),
            withLabelText(valueOf(lastBid)),
            withLabelText(statusText)));
  }

  public void hasColumnTitles() {
    JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
    headers.hasHeaders(
        matching(
            withLabelText("Item"),
            withLabelText("Last Price"),
            withLabelText("Last Bid"),
            withLabelText("State")));
  }
}
