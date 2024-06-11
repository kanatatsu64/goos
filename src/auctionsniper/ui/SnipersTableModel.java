package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.interfaces.SniperListener;
import auctionsniper.interfaces.PortfolioListener;
import auctionsniper.xmpp.SwingThreadSniperListener;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
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

  private ArrayList<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
  @SuppressWarnings("unused")
  private ArrayList<AuctionSniper> notToBeGCd = new ArrayList<AuctionSniper>();

  @Override
  public int getColumnCount() {
    return Column.values().length;
  }

  @Override
  public int getRowCount() {
    return snapshots.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
  }

  @Override
  public String getColumnName(int columnIndex) {
    return Column.at(columnIndex).name;
  }

  public void sniperStateChanged(SniperSnapshot snapshot) {
    int row = rowMatching(snapshot);
    snapshots.set(row, snapshot);
    fireTableRowsUpdated(row, row);
  }

  public void sniperAdded(SniperSnapshot snapshot) {
    snapshots.add(snapshot);
    int row = getRowCount() - 1;
    fireTableRowsInserted(row, row);
  }

  public void sniperAdded(AuctionSniper sniper) {
    notToBeGCd.add(sniper);

    SniperListener sniperListener = new SwingThreadSniperListener(this);
    sniper.addSniperListener(sniperListener);
  }

  static public String textFor(SniperState state) {
    return STATUS_TEXT[state.ordinal()];
  }

  private int rowMatching(SniperSnapshot snapshot) {
    for (int i = 0; i < snapshots.size(); i++) {
      if (snapshot.isForSameItemAs(snapshots.get(i))) {
        return i;
      }
    }

    throw new Defect("Cannot find match for " + snapshot);
  }
}
