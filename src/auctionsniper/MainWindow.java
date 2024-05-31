package auctionsniper;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow extends JFrame {
  public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
  public static final String SNIPERS_TABLE_NAME = "snipers table";

  private final SnipersTableModel snipers = new SnipersTableModel();

  public MainWindow() {
    super("Auction Sniper");
    setName(MAIN_WINDOW_NAME);
    fillContentPane(makeSnipersTable());
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
    snipers.sniperStateChanged(sniperSnapshot);
  }

  private void fillContentPane(JTable snipersTable) {
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
  }

  private JTable makeSnipersTable() {
    final JTable snipersTable = new JTable(snipers);
    snipersTable.setName(SNIPERS_TABLE_NAME);
    return snipersTable;
  }
}
