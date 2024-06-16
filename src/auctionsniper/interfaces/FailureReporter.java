package auctionsniper.interfaces;

public interface FailureReporter {
  void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
