package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.interfaces.UserRequestListener;

public class MainWindow extends JFrame {
  public static final String APPLICATION_TITLE = "Auction Sniper";
  public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
  public static final String SNIPERS_TABLE_NAME = "snipers table";
  public static final String NEW_ITEM_ID_NAME = "new item id";
  public static final String NEW_ITEM_STOP_PRICE_NAME = "new stop price";
  public static final String JOIN_BUTTON_NAME = "join button";

  private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

  public MainWindow(SniperPortfolio portfolio) {
    super(APPLICATION_TITLE);

    setName(MAIN_WINDOW_NAME);
    fillContentPane(makeSnipersTable(portfolio), makeControls());
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void addUserRequestListener(UserRequestListener listener) {
    userRequests.addListener(listener);
  }

  private void fillContentPane(JTable snipersTable, JPanel controls) {
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    contentPane.add(controls, BorderLayout.NORTH);
    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
  }

  private JTable makeSnipersTable(SniperPortfolio portfolio) {
    final SnipersTableModel model = new SnipersTableModel();
    portfolio.addPortfolioListener(model);

    final JTable snipersTable = new JTable(model);
    snipersTable.setName(SNIPERS_TABLE_NAME);
    return snipersTable;
  }

  private JPanel makeControls() {
    // <JPanel manager="flow">
    //// <JTextField name={NEW_ITEM_ID_NAME} columns=25 />
    //// <JTextField name={NEW_ITEM_STOP_PRICE_NAME} columns=25 />
    //// <JButton name={JOIN_BUTTON_NAME}>Join Auction</JButton>
    // </JPanel>
    JPanel controls = new JPanel(new FlowLayout());

    final JTextField itemIdField = new JTextField();
    itemIdField.setColumns(25);
    itemIdField.setName(NEW_ITEM_ID_NAME);
    controls.add(itemIdField);

    final JTextField stopPriceField = new JTextField();
    stopPriceField.setColumns(25);
    stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
    controls.add(stopPriceField);

    JButton joinAuctionButton = new JButton("Join Auction");
    joinAuctionButton.setName(JOIN_BUTTON_NAME);
    joinAuctionButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
      }

      private String itemId() {
        return itemIdField.getText();
      }

      private int stopPrice() {
        return Integer.decode(stopPriceField.getText());
      }
    });
    controls.add(joinAuctionButton);

    return controls;
  }
}
