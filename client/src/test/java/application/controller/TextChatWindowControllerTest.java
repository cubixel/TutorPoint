package application.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import application.controller.services.MainConnection;
import application.controller.services.TextChatService;
import application.model.Message;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.mockito.Mock;

public class TextChatWindowControllerTest {

  /**
   * CLASS DESCRIPTION: This class tests the TextChatWindowController. It tests the functionality of
   * the text chat.
   *
   * @author Oli Clarke
   */

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected TextChatService textChatServiceMock;

  protected TextChatWindowController textChatWindowController;

  protected Button textChatSendButton;

  protected TextField textChatInput;

  /**
   * Tests that the Text Chat has been initialised and is not null.
   */
  public void testTextChatInitialise() {
    assertNotNull(textChatWindowController.getMessage());

    System.out.println("Text Chat Initialisation - Test Complete");
  }

  /**
   * Test the send JavaFX button.
   */
  public void testSendButton() {

    Platform.runLater(() -> {
      // Test does send when button pressed and characters in text field.
      textChatInput.setText("Testing That Text Chat Button Is Working");
      textChatSendButton.fire();
      verify(textChatServiceMock).sendSessionUpdates(any());

      // Test does not send when empty textfield.
      textChatInput.clear();
      textChatSendButton.fire();
      verify(textChatServiceMock, never()).sendSessionUpdates(any());
    });

    System.out.println("Send Button - Test Complete");
  }

  /**
   * Test the 'ENTER' key send in textfield.
   */
  public void testEnterKey() {
    Platform.runLater(() -> {

    // Test does send when 'ENTER' key pressed and characters in text field.
    KeyEvent enterKey = new KeyEvent(null, textChatInput,
        KeyEvent.KEY_PRESSED, "", "",
        KeyCode.ENTER, false, false, false, false);

    textChatInput.fireEvent(enterKey);
    verify(textChatServiceMock).sendSessionUpdates(any());

    // Test does not send when empty textfield.
    textChatInput.clear();
    textChatInput.fireEvent(enterKey);

    verify(textChatServiceMock, never()).sendSessionUpdates(any());

    });
    System.out.println("'ENTER' Send Key Event - Test Complete");
  }

}
