package auctionsniper.xmpp;

import static java.lang.String.format;

import java.util.logging.Logger;

import auctionsniper.interfaces.FailureReporter;

public class LoggingXMPPFailureReporter implements FailureReporter {
  private final Logger logger;

  public LoggingXMPPFailureReporter(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
    logger.severe(
        format("<%s> Could not translate message \"%s\" because \"%s\"",
            auctionId,
            failedMessage,
            exception));
  }
}
