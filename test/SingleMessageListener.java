package test;

import java.util.concurrent.ArrayBlockingQueue;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class SingleMessageListener implements MessageListener {
  private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

  public void processMessage(Chat chat, Message message) {
    messages.add(message);
  }

  public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
    final Message message = messages.poll(10, SECONDS);
    assertThat("Message", message, is(notNullValue()));
    assertThat(message.getBody(), messageMatcher);
  }
}
