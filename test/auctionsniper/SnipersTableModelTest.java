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

import auctionsniper.MainWindow;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.SnipersTableModel;
import auctionsniper.SnipersTableModel.Column;

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
        oneOf(listener).tableChanged(with(aRowChangedEvent()));
      }
    });

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

  private void assertColumnEquals(Column column, Object expected) {
    final int rowIndex = 0;
    final int columnIndex = column.ordinal();
    assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
  }

  private Matcher<TableModelEvent> aRowChangedEvent() {
    return samePropertyValuesAs(new TableModelEvent(model, 0));
  }
}
