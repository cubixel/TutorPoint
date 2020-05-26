package application.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import application.model.Message;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.mockito.Mock;

/**
 * Test class for the MessageManager.
 *
 * @author Oliver Clarke
 * @author James Gardner
 * @see Message
 * @see MessageManager
 */
public class MessageManagerTest {

  @Mock
  protected Message messageMock;

  protected MessageManager messageManager;
  protected VBox textChat;
  protected ScrollPane textChatScrollPane;


  /**
   * This is testing the addMessage method.
   */
  public void addMessageTest() {
    when(messageMock.getUserName()).thenReturn("UserName");
    when(messageMock.getMsg()).thenReturn("This is a test message");
    Platform.runLater(() -> messageManager.addMessage(messageMock));

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    //Get last message.
    assertEquals(messageMock, messageManager.getLastMessage());

    assertEquals(1, textChat.getChildren().size());
  }
}