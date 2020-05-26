package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  private WhiteboardService whiteboardService;
  private String returnedString;
  volatile boolean threadDone;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private Whiteboard whiteboard;

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
   * This method ends the JavaFX runtime.
   */
  @AfterAll
  public static void cleanUp() {
    Platform.exit();
  }

  /**
   * Initialises Mocks, sets up Mock return values when called and creates
   * an instance of the UUT.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    whiteboardService = new WhiteboardService(mainConnectionMock, whiteboard, 0, 0);

    threadDone = false;
  }

  @Test
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

  @Test
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

  @Test
  public void failedBy() {
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