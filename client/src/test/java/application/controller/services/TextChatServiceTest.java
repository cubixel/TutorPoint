package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.TextChatMessageResult;
import application.model.Message;
import application.model.managers.MessageManager;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  private TextChatService textChatService;
  private String returnedString;
  volatile boolean threadDone;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private Message message;                  // Message for service.

  @Mock
  private MessageManager messageManager;    // Locally stored messages.

  /**
   * Sets up the JavaFX Toolkit for running JavaFX processes on.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }

  /**
   * Initialises Mocks, sets up Mock return values when called and creates
   * an instance of the UUT.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    textChatService = new TextChatService(message, messageManager, mainConnectionMock, "testUserName", 0, 0);

    threadDone = false;
  }

  @Test
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

  @Test
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
