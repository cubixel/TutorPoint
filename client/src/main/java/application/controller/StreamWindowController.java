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
  private VBox textChatHolder;

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
  void startStreamingButton() {

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

    if (!sessionRequestService.isRunning()) {
      sessionRequestService.reset();
      sessionRequestService.start();
    } else {
      log.warn("UpdateStreamingStatusService is still running");
    }

    sessionRequestService.setOnSucceeded(event -> {
      SessionRequestResult result = sessionRequestService.getValue();

      switch (result) {
        case SESSION_REQUEST_TRUE:
          log.info("SESSION_REQUEST_TRUE");
          break;
        case SESSION_REQUEST_FALSE:
          log.error("SESSION_REQUEST_FALSE");
          break;
        case END_SESSION_REQUEST_SUCCESS:
          log.info("END_SESSION_REQUEST_SUCCESS");
          break;
        case END_SESSION_REQUEST_FAILED:
          log.error("END_SESSION_REQUEST_FAILED");
          break;
        case FAILED_BY_TUTOR_NOT_ONLINE:
          log.error("FAILED_BY_TUTOR_NOT_ONLINE");
          break;
        case FAILED_BY_TUTOR_NOT_LIVE:
          log.error("FAILED_BY_TUTOR_NOT_LIVE");
          break;
        case FAILED_BY_NETWORK:
          log.error("FAILED_BY_NETWORK");
          break;
        case FAILED_BY_UNKNOWN_ERROR:
          log.error("FAILED_BY_UNKNOWN_ERROR");
          break;
        default:
          log.error("FAILED_BY_DEFAULT_UNKNOWN");
      }
    });
  }

  @FXML
  void disconnectButtonAction() {
    //sessionRequest(true);
    // TODO tell the server the client is leaving the
    //  session. sessionRequest(true);
    //  if its a tutor then reset the Stream page and to show
    //  the option to start streaming.
    //  If it is a user then close the stream screen and return
    //  to home page.
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
    // TODO Use userID as session identifier
    log.info("Created new sessionID: " + sessionID);

    // TODO send a request to join the session of sessionID
    //  on the server side if this request is the userID then it creates a new session
    //  else it searches through the loggedInClients, checks if they are live and if
    //  they are (which should be the case as the only way to get to this page is as
    //  a user is via the livetutors section) then add them to that session.

    if (!isHost) {
      /* If it is not the host as determined when constructor called then do changes needed
       * for showing only the viewer version of the stream such as removing the streamButton. */
      streamButton.setVisible(false);
    } else {
      disconnectButton.setVisible(false);
    }

    sessionRequest(false);

    //noinspection StatementWithEmptyBody
    while (!sessionRequestService.isFinished()) {

    }

    // TODO Media Players Need Scaling

    try {
      //viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
      viewFactory.embedWhiteboardWindow(anchorPaneMultiViewWhiteboard, account.getUserID(), sessionID);
      viewFactory.embedWhiteboardWindow(anchorPaneWhiteboard, account.getUserID(), sessionID);
      viewFactory.embedPresentationWindow(anchorPanePresentation);
      //viewFactory.embedTextChatWindow(textChatHolder);
      // TODO embedTextChat error
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
