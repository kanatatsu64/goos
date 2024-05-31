package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
  public enum Column {
    ITEM_IDENTIFIER,
    LAST_PRICE,
    LAST_BID,
    SNIPER_STATE;

    public static Column at(int offset) {
      return values()[offset];
    }
  }

  private static String[] STATUS_TEXT = {
      MainWindow.STATUS_JOINING,
      MainWindow.STATUS_BIDDING,
      MainWindow.STATUS_WINNING,
      MainWindow.STATUS_LOST,
      MainWindow.STATUS_WON
  };

  private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
  private String statusText = MainWindow.STATUS_JOINING;
  private SniperSnapshot sniperSnapshot = STARTING_UP;

  @Override
  public int getColumnCount() {
    return Column.values().length;
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch (Column.at(columnIndex)) {
      case ITEM_IDENTIFIER:
        return sniperSnapshot.itemId;
      case LAST_PRICE:
        return sniperSnapshot.lastPrice;
      case LAST_BID:
        return sniperSnapshot.lastBid;
      case SNIPER_STATE:
        return statusText;
      default:
        throw new IllegalArgumentException("No column at " + columnIndex);
    }
  }

  public void setStatusText(String newStatusText) {
    statusText = newStatusText;
    fireTableRowsUpdated(0, 0);
  }

  public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
    sniperSnapshot = newSniperSnapshot;
    statusText = STATUS_TEXT[sniperSnapshot.state.ordinal()];
    fireTableRowsUpdated(0, 0);
  }
}