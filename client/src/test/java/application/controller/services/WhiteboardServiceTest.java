package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the WhiteboardService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author Oliver Still
 * @see WhiteboardService
 */
public class WhiteboardServiceTest {

  protected WhiteboardService whiteboardService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected Whiteboard whiteboard;

  /**
   * This is the successfulResultTest.
   */
  public void successfulResultTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(WhiteboardRenderResult.WHITEBOARD_RENDER_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {

      WhiteboardRenderResult result = whiteboardService.sendSessionPackage();

      assertEquals(WhiteboardRenderResult.WHITEBOARD_RENDER_SUCCESS, result);
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

      WhiteboardRenderResult result = whiteboardService.sendSessionPackage();

      assertEquals(WhiteboardRenderResult.FAILED_BY_NETWORK, result);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByCredentials.
   */
  public void failedByCredentials() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(WhiteboardRenderResult.FAILED_BY_CREDENTIALS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {

      WhiteboardRenderResult result = whiteboardService.sendSessionPackage();

      assertEquals(WhiteboardRenderResult.FAILED_BY_CREDENTIALS, result);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}