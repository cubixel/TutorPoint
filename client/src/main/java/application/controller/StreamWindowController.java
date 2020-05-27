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

/**
 * The StreamWindowController contains the control methods
 * for the FXML StreamWindow page. This includes embedded
 * windows for all the Stream modules. It enables a Tutor
 * to start a live stream that other users can join.
 *
 * <p>Upon starting the TutorPoint application as a Tutor
 * a StreamWindow is created and a Session is started with the
 * Server. This Session must be set to live for other users to
 * join.
 *
 * <p>If the TutorPoint application is started as a User
 * a StreamWindow is not created but one can be joined by
 * selecting a live tutor on the HomeWindow. This will generate
 * a StreamWindow and join a currently running session on the
 * server.
 *
 * @author James Gardner
 * @author Oliver Still
 * @author Stijn Marynissen
 * @author Daniel Bishop
 */
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

  @FXML
  private VBox sidePanelVbox;

  private final Account account;
  private UpdateStreamingStatusService updateStreamingStatusService;
  private SessionRequestService sessionRequestService;
  private final int sessionID;
  private final boolean isHost;
  private boolean isLive;

  private static final Logger log = LoggerFactory.getLogger("StreamWindowController");

  /**
   * This is the default constructor. StreamWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param account
   *        The Account of the User who is logged in
   *
   * @param sessionID
   *        The sessionID of this Stream, same as UserID of Account if Host
   *
   * @param isHost
   *        {@code true} if the user is Tutor and Hosting this stream, {@code false} otherwise
   */
  public StreamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, int sessionID, Boolean isHost) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    this.sessionID = sessionID;
    this.isHost = isHost;
    this.isLive = false;
  }

  /**
   * This is the constructor used for testing. StreamWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param account
   *        The Account of the User who is logged in
   *
   * @param sessionID
   *        The sessionID of this Stream, same as UserID of Account if Host
   *
   * @param isHost
   *        {@code true} if the user is Tutor and Hosting this stream, {@code false} otherwise
   *
   * @param primaryTabPane
   *        A JavaFX TabPane for swapping between modules
   *
   * @param anchorPaneMultiViewVideo
   *        A JavaFX AnchorPane
   *
   * @param anchorPaneMultiViewPresentation
   *        A JavaFX AnchorPane
   *
   * @param anchorPaneMultiViewWhiteboard
   *        A JavaFX AnchorPane
   *
   * @param anchorPaneVideo
   *        A JavaFX AnchorPane
   *
   * @param webcamHolder
   *        A JavaFX AnchorPane
   *
   * @param textChatHolder
   *        A JavaFX AnchorPane
   *
   * @param anchorPanePresentation
   *        A JavaFX AnchorPane
   *
   * @param anchorPaneWhiteboard
   *        A JavaFX AnchorPane
   *
   * @param masterPane
   *        A JavaFX AnchorPane
   *
   * @param resizePane
   *        A JavaFX Pane
   *
   * @param streamButton
   *        A JavaFX Button for starting and stopping a stream
   *
   * @param disconnectButton
   *        A JavaFX Button for a user to disconnect from a stream
   *
   * @param resetStream
   *        A JavaFX Button for a tutor to reset a stream
   *
   * @param sidePanelVbox
   *        A JavaFX VBox containing the buttons to start a stream
   */
  public StreamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, int sessionID, Boolean isHost,
      Boolean isLive, TabPane primaryTabPane, AnchorPane anchorPaneMultiViewVideo,
      AnchorPane anchorPaneMultiViewPresentation, AnchorPane anchorPaneMultiViewWhiteboard,
      AnchorPane anchorPaneVideo, AnchorPane webcamHolder, AnchorPane textChatHolder,
      AnchorPane anchorPanePresentation, AnchorPane anchorPaneWhiteboard, AnchorPane masterPane,
      Pane resizePane, Button streamButton, Button disconnectButton,
      Button resetStream, VBox sidePanelVbox) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    this.sessionID = sessionID;
    this.isHost = isHost;
    this.isLive = isLive;
    this.primaryTabPane = primaryTabPane;
    this.anchorPaneMultiViewVideo = anchorPaneMultiViewVideo;
    this.anchorPaneMultiViewPresentation = anchorPaneMultiViewPresentation;
    this.anchorPaneMultiViewWhiteboard = anchorPaneMultiViewWhiteboard;
    this.anchorPaneVideo = anchorPaneVideo;
    this.webcamHolder = webcamHolder;
    this.textChatHolder = textChatHolder;
    this.anchorPanePresentation = anchorPanePresentation;
    this.anchorPaneWhiteboard = anchorPaneWhiteboard;
    this.masterPane = masterPane;
    this.resizePane = resizePane;
    this.streamButton = streamButton;
    this.disconnectButton = disconnectButton;
    this.resetStream = resetStream;
    this.sidePanelVbox = sidePanelVbox;

    sidePanelVbox.getChildren().add(disconnectButton);
    sidePanelVbox.getChildren().add(streamButton);
    sidePanelVbox.getChildren().add(resetStream);
    sidePanelVbox.getChildren().add(webcamHolder);
    sidePanelVbox.getChildren().add(textChatHolder);
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
  void disconnectButtonAction() {
    log.debug("DISCONNECT BUTTON PRESSED ********");
    // Stop current presentation if one exists
    if (getMainConnection().getListener().hasCorrectPresentationWindowControllers()) {
      log.info("Clearing presentation on exit");
      getMainConnection().getListener().getPresentationWindowControllers().forEach(
          PresentationWindowController::clearPresentation);
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
   * Used to change the streaming state on the server.
   * Either going live or ending the live stream.
   */
  @FXML
  public void changeStreamingStateButton() {

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
          // TODO Any other setup that needs finishing
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

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (!isHost) {
      /* If it is not the host as determined when constructor called then do changes needed
       * for showing only the viewer version of the stream such as not showing the
       * start stream button but showing the disconnect button instead. */
      sidePanelVbox.getChildren().remove(1);
      sidePanelVbox.getChildren().remove(1);
      log.info("Joining Session with ID: " + sessionID);
    } else {
      sidePanelVbox.getChildren().remove(0);
      log.info("Creating Session with ID: " + sessionID);
    }

    // Send a session request to start/join the session.
    sessionRequest(false);

    initWindows();
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

          // TODO Potential here if using resetStreamTab to get stuck in some infinite
          //  loop where you can never join a session so keeps resetting and trying
          //  to join a session resetStreamTab();

          // TODO - Send back to Home Tab.

          break;
        case END_SESSION_REQUEST_SUCCESS:
          log.info("END_SESSION_REQUEST_SUCCESS");
          resetStreamTab();
          break;
        case END_SESSION_REQUEST_FAILED:
          // TODO Handle issue better than just reporting the error
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

  private void initWindows() {
    try {
      viewFactory.embedWhiteboardWindow(anchorPaneWhiteboard, account.getUserID(), sessionID);
      viewFactory.embedPresentationWindow(anchorPanePresentation, isHost);
      viewFactory.embedMediaPlayerWindow(anchorPaneVideo);
      viewFactory.embedTextChatWindow(textChatHolder, account.getUsername(),
          account.getUserID(), sessionID);
      viewFactory.embedWebcamWindow(webcamHolder,account.getUserID());

      // TODO Currently the multi-view is not implemented
      // viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
      // viewFactory.embedWhiteboardWindow(anchorPaneMultiViewWhiteboard,
      //     account.getUserID(), sessionID);
      // viewFactory.embedPresentationWindow(anchorPaneMultiViewPresentation, isHost);
      // viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
    } catch (IOException e) {
      log.error("Could not embed stages into Stream Window", e);
    }
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
}