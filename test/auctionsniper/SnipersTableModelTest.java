package test.auctionsniper;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SnipersTableModel.Column;

public class SnipersTableModelTest {

  private final Mockery context = new Mockery();
  private TableModelListener listener = context.mock(TableModelListener.class);
  private final SnipersTableModel model = new SnipersTableModel();

  @Before
  public void attachModelListner() {
    model.addTableModelListener(listener);
  }

  @Test
  public void hasEnoughColumns() {
    assertThat(model.getColumnCount(), equalTo(Column.values().length));
  }

  @Test
  public void setsSniperValuesInColumns() {
    context.checking(new Expectations() {
      {
        oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        oneOf(listener).tableChanged(with(aRowChangedEvent()));
      }
    });

    model.sniperAdded(SniperSnapshot.joining("item id"));
    model.sniperStateChanged(new SniperSnapshot("item id", 565, 666, SniperState.BIDDING));

    assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
    assertColumnEquals(Column.LAST_PRICE, 565);
    assertColumnEquals(Column.LAST_BID, 666);
    assertColumnEquals(Column.SNIPER_STATE, "Bidding");
  }

  @Test
  public void setsUpColumnHeadings() {
    for (Column column : Column.values()) {
      assertEquals(column.name, model.getColumnName(column.ordinal()));
    }
  }

  @Test
  public void notifiesListenersWhenaddingASniper() {
    SniperSnapshot joining = SniperSnapshot.joining("item123");
    context.checking(new Expectations() {
      {
        oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
      }
    });

    assertEquals(0, model.getRowCount());

    model.sniperAdded(joining);

    assertEquals(1, model.getRowCount());
    assertRowMatchesSnapshot(0, joining);
  }

  @Test
  public void holdsSnipersInAdditionOrder() {
    context.checking(new Expectations() {
      {
        ignoring(listener);
      }
    });

    model.sniperAdded(SniperSnapshot.joining("item 0"));
    model.sniperAdded(SniperSnapshot.joining("item 1"));

    assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
    assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
  }

  private void assertColumnEquals(Column column, Object expected) {
    final int rowIndex = 0;
    final int columnIndex = column.ordinal();
    assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
  }

  private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
    assertColumnEquals(Column.ITEM_IDENTIFIER, snapshot.itemId);
    assertColumnEquals(Column.LAST_PRICE, snapshot.lastPrice);
    assertColumnEquals(Column.LAST_BID, snapshot.lastBid);
    assertColumnEquals(Column.SNIPER_STATE, SnipersTableModel.textFor(snapshot.state));
  }

  private Matcher<TableModelEvent> aRowChangedEvent() {
    return samePropertyValuesAs(new TableModelEvent(model, 0));
  }

  private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
    return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, -1, 1));
  }

  private Object cellValue(int row, Column column) {
    return model.getValueAt(row, column.ordinal());
  }
}
