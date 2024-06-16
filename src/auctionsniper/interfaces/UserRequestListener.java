package auctionsniper.interfaces;

import java.util.EventListener;

import auctionsniper.Item;

public interface UserRequestListener extends EventListener {
  void joinAuction(Item item);
}
