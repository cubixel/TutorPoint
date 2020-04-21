package application.controller;

import application.controller.enums.AccountLoginResult;
import application.controller.enums.StreamingStatusUpdateResult;
import application.controller.services.MainConnection;
import application.controller.services.SessionRequestService;
import application.controller.services.UpdateStreamingStatusService;
import application.model.Account;
import application.view.ViewFactory;
import java.io.FileWriter;
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
import javafx.stage.Stage;
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

  private BaseController mediaPlayerController;

  private BaseController whiteboardWindowContoller;

  private BaseController presentationWindowController;

  private BaseController textChatWindowController;

  private Account account;

  private SessionRequestService sessionRequestService;
  private UpdateStreamingStatusService updateStreamingStatusService;

  private boolean streamingStatus = false;

  private int sessionID;

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
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    this.updateStreamingStatusService = new UpdateStreamingStatusService(mainConnection);
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
          if (streamButton.getText().equals("Stop Streaming")) {
            streamButton.setText("Start Streaming");
            log.info("User is now streaming");
          } else {
            streamButton.setText("Stop Streaming");
            log.info("User is no longer streaming");
          }
          streamingStatus = !streamingStatus;
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



    // TODO will already have a session id that applies to all
    //  elements from initialisation of this controller just need to become live

    /* This could involve a session id
     * setting the tutor creating the
     * session as live on the database.
     *
     * The session id should be the same
     * across all elements, presentation
     * whiteboard, video, text chat.
     *
     * update the livesession on database
     * with the session id.
     * */
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
    // TODO Go generate a session ID from the server.
    // Use this ID to instantiate the whiteboard, presentation, text chat and media player
    // But this shouldn't mean the session is live yet. Just that it is set up.

    // The session ID should be able to connect users however as the two whiteboards on this end
    // currently communicate via the server. Just that it isn't public to other users until
    // the tutor chooses to go live.

    sessionID = account.getUserID();

    // TODO Media Players Need Scaling

    try {
      viewFactory.embedMediaPlayerWindow(anchorPaneMultiViewVideo);
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
