package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.interfaces.SniperListener;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
  public enum Column {
    ITEM_IDENTIFIER("Item") {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.itemId;
      }
    },
    LAST_PRICE("Last Price") {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.lastPrice;
      }
    },
    LAST_BID("Last Bid") {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return snapshot.lastBid;
      }
    },
    SNIPER_STATE("State") {
      @Override
      public Object valueIn(SniperSnapshot snapshot) {
        return SnipersTableModel.textFor(snapshot.state);
      }
    };

    public final String name;

    private Column(String name) {
      this.name = name;
    }

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

  @Override
  public String getColumnName(int columnIndex) {
    return Column.at(columnIndex).name;
  }

  public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
    snapshot = newSniperSnapshot;
    fireTableRowsUpdated(0, 0);
  }

  static public String textFor(SniperState state) {
    return STATUS_TEXT[state.ordinal()];
  }
}
