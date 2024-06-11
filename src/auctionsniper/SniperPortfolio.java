package auctionsniper;

import java.util.ArrayList;

import auctionsniper.interfaces.PortfolioListener;
import auctionsniper.interfaces.SniperCollector;
import auctionsniper.ui.Announcer;

public class SniperPortfolio implements SniperCollector {
  private final Announcer<PortfolioListener> portfolioListeners = new Announcer<>(PortfolioListener.class);
  private ArrayList<AuctionSniper> snipers = new ArrayList<>();

  public void addPortfolioListener(PortfolioListener listener) {
    portfolioListeners.addListener(listener);
  }

  public void addSniper(AuctionSniper sniper) {
    snipers.add(sniper);
    portfolioListeners.announce().sniperAdded(sniper);
  }
}
