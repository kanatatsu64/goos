package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class MainWindow extends JFrame {
  public static final String APPLICATION_TITLE = "Auction Sniper";
  public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
  public static final String SNIPERS_TABLE_NAME = "snipers table";

  public MainWindow(SnipersTableModel snipers) {
    super(APPLICATION_TITLE);

    setName(MAIN_WINDOW_NAME);
    fillContentPane(makeSnipersTable(snipers));
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void fillContentPane(JTable snipersTable) {
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
  }

  private JTable makeSnipersTable(AbstractTableModel model) {
    final JTable snipersTable = new JTable(model);
    snipersTable.setName(SNIPERS_TABLE_NAME);
    return snipersTable;
  }
}
