package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
  public enum Column {
    ITEM_IDENTIFIER {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.itemId;
      }
    },
    LAST_PRICE {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.lastPrice;
      }
    },
    LAST_BID {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.lastBid;
      }
    },
    SNIPER_STATE {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return SnipersTableModel.textFor(snapshot.state);
      }
    };

    public static Column at(int offset) {
      return values()[offset];
    }

    abstract public Object valueIn(SniperSnapshot snapshot);
  }

  private static String[] STATUS_TEXT = {
      "Joining",
      "Bidding",
      "Winning",
      "Lost",
      "Won"
  };

  private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
  private SniperSnapshot snapshot = STARTING_UP;

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
    return Column.at(columnIndex).valueIn(snapshot);
  }

  public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
    snapshot = newSniperSnapshot;
    fireTableRowsUpdated(0, 0);
  }

  static public String textFor(SniperState state) {
    return STATUS_TEXT[state.ordinal()];
  }
}
