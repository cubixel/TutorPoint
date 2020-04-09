package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class StreamWindowController extends BaseController implements Initializable {

  @FXML
  private TabPane primaryTabPane;

  @FXML
  private AnchorPane anchorPaneOne;

  @FXML
  private AnchorPane anchorPaneTwo;

  @FXML
  private AnchorPane anchorPaneThree;

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
  private AnchorPane pane;

  BaseController mediaPlayerController;

  BaseController whiteboardWindowContoller;

  BaseController presentationWindowController;

  BaseController textChatWindowController;

  Account account;

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
  }


  @FXML
  private void setCursorDefault() {
    pane.getScene().getRoot().setCursor(Cursor.DEFAULT);
  }

  @FXML
  private void setCursorHResize() {
    pane.getScene().getRoot().setCursor(Cursor.H_RESIZE);
  }

  @FXML
  private void setCursorVResize() {
    pane.getScene().getRoot().setCursor(Cursor.V_RESIZE);
  }

  @FXML
  private void moveHDivider(MouseEvent event) {
    if ((event.getSceneX() < (masterPane.getWidth() * 0.4))
        && (event.getSceneX() > (masterPane.getWidth() * 0.2))) {
      anchorPaneThree.setPrefWidth(masterPane.getWidth() - event.getSceneX() - 200);
    }
  }

  @FXML
  private void moveVDivider(MouseEvent event) {
    if ((event.getSceneY() < (masterPane.getHeight() * 0.8))
        && (event.getSceneY() > (masterPane.getHeight() * 0.2))) {
      anchorPaneOne.setPrefHeight(event.getSceneY() - 40);
    }
  }

  @FXML
  void startStreamingButton() {
    // TODO Set up a session instance
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
    // TODO Media Players Need Scaling

    try {
      viewFactory.embedMediaPlayerWindow(anchorPaneOne);
      //viewFactory.embedWhiteboardWindow(anchorPaneThree);
      //viewFactory.embedWhiteboardWindow(anchorPaneWhiteboard);
      viewFactory.embedPresentationWindow(anchorPanePresentation);
      //viewFactory.embedTextChatWindow(pane);
      // TODO embedTextChat error
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
