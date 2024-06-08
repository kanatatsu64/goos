package auctionsniper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SniperSnapshot {
  public final String itemId;
  public final int lastPrice;
  public final int lastBid;
  public final SniperState state;

  public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
    this.itemId = itemId;
    this.lastPrice = lastPrice;
    this.lastBid = lastBid;
    this.state = state;
  }

  public static SniperSnapshot joining(String itemId) {
    return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
  }

  public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
    return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
  }

  public SniperSnapshot winning(int newLastPrice) {
    return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
  }

  public SniperSnapshot close() {
    return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public boolean isForSameItemAs(SniperSnapshot snapshot) {
    return this.itemId == snapshot.itemId;
  }
}
