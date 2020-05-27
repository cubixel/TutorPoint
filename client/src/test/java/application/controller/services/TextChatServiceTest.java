package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.enums.TextChatMessageResult;
import application.model.Message;
import application.model.managers.MessageManager;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the TextChatService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author Oliver Still
 * @see TextChatService
 */
public class TextChatServiceTest {

  protected TextChatService textChatService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected Message message;                  // Message for service.

  @Mock
  protected MessageManager messageManager;    // Locally stored messages.

  /**
   * This is the successfulResultTest.
   */
  public void successfulResultTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {

      TextChatMessageResult result = textChatService.send();

      assertEquals(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS, result);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByNetwork.
   */
  public void failedByNetwork() {
    // Setting Mock return value.
    try {
      when(mainConnectionMock.listenForString()).thenThrow(IOException.class);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {

      TextChatMessageResult result = textChatService.send();

      assertEquals(TextChatMessageResult.FAILED_BY_NETWORK, result);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

}
