package application.controller;

import application.controller.enums.SessionRequestResult;
import application.controller.enums.StreamingStatusUpdateResult;
import application.controller.services.MainConnection;
import application.controller.services.SessionRequestService;
import application.controller.services.UpdateStreamingStatusService;
import application.model.Account;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StreamWindowController extends BaseController implements Initializable {

  @FXML
  private TabPane primaryTabPane;

  @FXML
  private AnchorPane anchorPaneMultiViewVideo;

  @FXML
  private AnchorPane anchorPaneMultiViewPresentation;

  @FXML
  private AnchorPane anchorPaneMultiViewWhiteboard;

  @FXML
  private AnchorPane anchorPaneVideo;

  @FXML
  private AnchorPane webcamHolder;

  @FXML
  private AnchorPane textChatHolder;

  @FXML
  private AnchorPane anchorPanePresentation;

  @FXML
  private AnchorPane anchorPaneWhiteboard;

  @FXML
  private AnchorPane masterPane;

  @FXML
  private Pane resizePane;

  @FXML
  private Button streamButton;

  @FXML
  private Button disconnectButton;

  @FXML
  private Button resetStream;

  private BaseController mediaPlayerController;

  private BaseController whiteboardWindowContoller;

  private BaseController presentationWindowController;

  private BaseController textChatWindowController;

  private Account account;

  private UpdateStreamingStatusService updateStreamingStatusService;
  private SessionRequestService sessionRequestService;

  private int sessionID;
  private boolean isHost;
  private boolean isLive;

  private static final Logger log = LoggerFactory.getLogger("StreamWindowController");

  /**
   * This is the default constructor. StreamWindowController
   * extends the BaseController class.
   *
   * @param viewFactory The viewFactory used for changing scenes
   * @param fxmlName The associated FXML file describing the Login Window
   * @param mainConnection The connection between client and server
   */
  public StreamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, int sessionID, Boolean isHost) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    this.sessionID = sessionID;
    this.isHost = isHost;
    this.isLive = false;
  }


  @FXML
  private void setCursorDefault() {
    resizePane.getScene().getRoot().setCursor(Cursor.DEFAULT);
  }

  @FXML
  private void setCursorHResize() {
    resizePane.getScene().getRoot().setCursor(Cursor.H_RESIZE);
  }

  @FXML
  private void setCursorVResize() {
    resizePane.getScene().getRoot().setCursor(Cursor.V_RESIZE);
  }

  @FXML
  private void moveHDivider(MouseEvent event) {
    if ((event.getSceneX() < (masterPane.getWidth() * 0.4))
        && (event.getSceneX() > (masterPane.getWidth() * 0.2))) {
      anchorPaneMultiViewWhiteboard.setPrefWidth(masterPane.getWidth() - event.getSceneX() - 200);
    }
  }

  @FXML
  private void moveVDivider(MouseEvent event) {
    if ((event.getSceneY() < (masterPane.getHeight() * 0.8))
        && (event.getSceneY() > (masterPane.getHeight() * 0.2))) {
      anchorPaneMultiViewVideo.setPrefHeight(event.getSceneY() - 40);
    }
  }

  @FXML
  void changeStreamingStateButton() {

    updateStreamingStatusService = new UpdateStreamingStatusService(getMainConnection(), !isLive);

    if (!updateStreamingStatusService.isRunning()) {
      updateStreamingStatusService.reset();
      updateStreamingStatusService.start();
    } else {
      log.warn("UpdateStreamingStatusService is still running");
    }

    updateStreamingStatusService.setOnSucceeded(event -> {
      StreamingStatusUpdateResult result = updateStreamingStatusService.getValue();

      switch (result) {
        case STATUS_UPDATE_SUCCESS:
          if (isLive) {
            streamButton.setText("Start Streaming");
            log.info("User " + account.getUsername() + " is no longer streaming");
            isLive = false;
          } else {
            streamButton.setText("Stop Streaming");
            log.info("User " + account.getUsername() + " is now streaming");
            isLive = true;
          }
          //TODO Any other setup
          break;
        case FAILED_ACCESSING_DATABASE:
          log.error("FAILED_ACCESSING_DATABASE");
          break;
        case FAILED_BY_UNEXPECTED_ERROR:
          log.error("FAILED_BY_UNEXPECTED_ERROR");
          break;
        case FAILED_BY_NETWORK:
          log.error("FAILED_BY_NETWORK");
          break;
        default:
          log.error("FAILED_BY_UNKNOWN");
      }
    });
  }

  private void sessionRequest(boolean leavingSession) {
    sessionRequestService = new SessionRequestService(getMainConnection(), account.getUserID(),
        sessionID, leavingSession, isHost);

    if (!leavingSession) {
      initWindows();
    }
    

    log.info("Session requested, leaving = " + leavingSession);
    if (!sessionRequestService.isRunning()) {
      sessionRequestService.reset();
      sessionRequestService.start();
    } else {
      log.warn("UpdateStreamingStatusService is still running");
    }

    sessionRequestService.setOnSucceeded(event -> {
      SessionRequestResult result = sessionRequestService.getValue();

      log.info("Session Request finished with result " + result);

      switch (result) {
        case SESSION_REQUEST_TRUE:
          log.info("SESSION_REQUEST_TRUE");
          break;
        case FAILED_SESSION_SETUP:
          log.error("SESSION_REQUEST_FALSE");
          // Undo creating windows
          resetStreamTab();
          
          // TODO Potential here if using resetStreamTab to get stuck in some infinite loop where you can
          //  never join a session so keeps resetting and trying to join a session
          //  resetStreamTab();
          // TODO - Send back to Home Tab.

          break;
        case END_SESSION_REQUEST_SUCCESS:
          log.info("END_SESSION_REQUEST_SUCCESS");
          resetStreamTab();
          break;
        case END_SESSION_REQUEST_FAILED:
          // TODO Handle issue
          log.error("END_SESSION_REQUEST_FAILED");
          break;
        case FAILED_BY_TUTOR_NOT_ONLINE:
          log.error("FAILED_BY_TUTOR_NOT_ONLINE");
          resetStreamTab();
          break;
        case FAILED_BY_TUTOR_NOT_LIVE:
          log.error("FAILED_BY_TUTOR_NOT_LIVE");
          resetStreamTab();
          break;
        case FAILED_BY_NETWORK:
          log.error("FAILED_BY_NETWORK");
          resetStreamTab();
          break;
        case FAILED_BY_UNKNOWN_ERROR:
          log.error("FAILED_BY_UNKNOWN_ERROR");
          resetStreamTab();
          break;
        default:
          log.error("FAILED_BY_DEFAULT_UNKNOWN");
          resetStreamTab();
      }
    });
  }

  /**
   * Closes the Stream tab and opens a new one if the
   * User is a Tutor.
   */
  private void resetStreamTab() {
    MainWindowController mainWindowController = (MainWindowController)
        viewFactory.getWindowControllers().get("MainWindowController");
    log.info("Removing Stream Tab");
    mainWindowController.getNavbar().getTabs().remove(4);
    if (account.getTutorStatus() == 1) {
      /* If it is a tutor then set up a new Stream Tab */
      AnchorPane anchorPaneStream = new AnchorPane();
      Tab tab = new Tab("Stream");
      tab.setContent(anchorPaneStream);
      mainWindowController.getPrimaryTabPane().getTabs().add(tab);
      log.info("Tutor has requested to end session they are in");
      log.info("Starting new private Session");
    }
  }

  @FXML
  void disconnectButtonAction() {
    log.debug("DISCONNECT BUTTON PRESSED ********");
    // Stop current presentation if one exists
    if (getMainConnection().getListener().hasCorrectPresentationWindowControllers()) {
      log.info("Clearing presentation on exit");
      getMainConnection().getListener().getPresentationWindowControllers().forEach((controller) -> {
        controller.clearPresentation();
      });
      getMainConnection().getListener().clearPresentationWindowControllers();
    }
    sessionRequest(true);
  }

  @FXML
  void resetStreamButtonAction() {
    // TODO used to clear/reset the stream window once you've finished
    //  a stream. This should either end the live stream and then reset the
    //  stream tab or just clear the Whiteboard, Presentation and Media Players
    //  so they are back to a new state.
  }


  /**
   * This instantiates controllers for all the components used on the
   * StreamWindow. Then links those controllers with their respective
   * FXML Files and embeds them into AnchorPanes.
   *
   * @param url The location used to resolve relative paths for the root
   *            object, or null if the location is not known.
   * @param resourceBundle The resources used to localize the root object,
   *                       or null if the root object was not localized.
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (!isHost) {
      // TODO Just setting the buttons as not visible isn't the neatest
      //  solution as it the button is still there. Need to remove the button
      //  from it's parent node but haven't done this as not sure yet where the buttons
      //  will end up on the window.
      /* If it is not the host as determined when constructor called then do changes needed
       * for showing only the viewer version of the stream such as not showing the
       * start stream button but showing the disconnect button instead. */
      streamButton.setVisible(false);
      resetStream.setVisible(false);
      log.info("Joining Session with ID: " + sessionID);
    } else {
      disconnectButton.setVisible(false);
      log.info("Creating Session with ID: " + sessionID);
    }

    // Send a session request to start/join the session.
    sessionRequest(false);

    initWindows();
  }

  private void initWindows() {
    try {
      // viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
      // viewFactory.embedWhiteboardWindow(anchorPaneMultiViewWhiteboard, account.getUserID(), sessionID);
      viewFactory.embedWhiteboardWindow(anchorPaneWhiteboard, account.getUserID(), sessionID);
      viewFactory.embedPresentationWindow(anchorPanePresentation, isHost);
      // viewFactory.embedPresentationWindow(anchorPaneMultiViewPresentation, isHost);
      viewFactory.embedMediaPlayerWindow(anchorPaneVideo);
      viewFactory.embedTextChatWindow(textChatHolder, account.getUsername(), account.getUserID(), sessionID);
      //Embed webcam
      viewFactory.embedWebcamWindow(webcamHolder, account.getUserID());
      // viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
    } catch (IOException e) {
      log.error("Could not embed stages into Stream Window", e);
    }
  }
}
