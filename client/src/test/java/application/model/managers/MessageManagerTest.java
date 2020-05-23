package application.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.Message;
import application.model.Subject;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Test class for the MessageManager.
 *
 * @author Oliver Still
 * @see Message
 * @see MessageManager
 */
public class MessageManagerTest {
  private MessageManager messageManager;

  @Mock
  Message messageMock;

  @Mock
  VBox vBox;

  @Mock
  ScrollPane scrollPane;

  @BeforeEach
  public void setUp() {
    initMocks(this);
    messageManager = new MessageManager(vBox, scrollPane);
  }

  @Test
  public void addMessageTest() {
    // Add message to local list of stored messages.
    messageManager.addMessage(messageMock);

    //Get last message.
    assertEquals(messageMock, messageManager.getLastMessage());
  }
}
