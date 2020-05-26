package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.services.MainConnection;
import application.model.Account;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the StreamWindowController. It tests
 * that the stream window is instantiated correctly and
 * that starting, ending and joining a stream works for
 * both users and tutors.
 *
 * @author James Gardner
 * @see StreamWindowController
 */
public class StreamWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  protected Account account;
  protected int sessionID;
  protected Boolean isHost;
  protected Boolean isLive;
  protected TabPane primaryTabPane;
  protected AnchorPane anchorPaneMultiViewVideo;
  protected AnchorPane anchorPaneMultiViewPresentation;
  protected AnchorPane anchorPaneMultiViewWhiteboard;
  protected AnchorPane anchorPaneVideo;
  protected AnchorPane webcamHolderOne;
  protected AnchorPane webcamHolderTwo;
  protected AnchorPane textChatHolder;
  protected AnchorPane anchorPanePresentation;
  protected AnchorPane anchorPaneWhiteboard;
  protected AnchorPane masterPane;
  protected Pane resizePane;
  protected Button streamButton;
  protected Button disconnectButton;
  protected Button resetStream;

  protected StreamWindowController streamWindowController;

  private static final Logger log = LoggerFactory.getLogger("StreamWindowControllerTest");

  /**
   * This is testing initialiseAsTutorHost.
   */
  public void initialiseAsTutorHost() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn("SESSION_REQUEST_TRUE");
    } catch (IOException e) {
      log.error("Failed to setup Mock MainConnection");
      fail(e);
    }
    isLive = false;
    Platform.runLater(() -> streamWindowController.initialize(null, null));

    long start = System.currentTimeMillis();
    long end = start + 2500;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }


    assertTrue(streamButton.isVisible());
    assertTrue(resetStream.isVisible());
    assertFalse(disconnectButton.isVisible());
  }

  /**
   * This is testing the changeStreamState method.
   */
  public void changeStreamState() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn("SESSION_REQUEST_TRUE",
          "STATUS_UPDATE_SUCCESS");
    } catch (IOException e) {
      log.error("Failed to setup Mock MainConnection");
      fail(e);
    }


    isLive = false;
    Platform.runLater(() -> streamWindowController.initialize(null, null));
    long start = System.currentTimeMillis();
    long end = start + 2500;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertFalse(isLive);

    Platform.runLater(() -> streamWindowController.changeStreamingStateButton());

    start = System.currentTimeMillis();
    end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertEquals("Stop Streaming", streamButton.getText());
  }
}