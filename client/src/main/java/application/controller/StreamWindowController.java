package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

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
  private AnchorPane anchorPaneWhiteboard;

  @FXML
  BaseController mediaPlayerController;

  @FXML
  BaseController whiteboardWindowContoller;

  @FXML
  BaseController presentationWindowController;

  @FXML
  private AnchorPane pane;

  @FXML
  private AnchorPane paneTwo;


  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   */
  public StreamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  @FXML
  void testButtonAction() {
    loadViews();
  }

  @FXML
  private void loadViews() {

    mediaPlayerController = new MediaPlayerController(getViewFactory(), "fxml/MediaPlayerWindow.fxml", getMainConnection());
    FXMLLoader fxmlLoader = new FXMLLoader(getViewFactory().getClass().getResource(mediaPlayerController.getFxmlName()));
    fxmlLoader.setController(mediaPlayerController);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneOne.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    whiteboardWindowContoller = new WhiteboardWindowController(getViewFactory(), "fxml/WhiteboardWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass().getResource(whiteboardWindowContoller.getFxmlName()));
    fxmlLoader.setController(whiteboardWindowContoller);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneThree.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    whiteboardWindowContoller = new WhiteboardWindowController(getViewFactory(), "fxml/WhiteboardWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass().getResource(whiteboardWindowContoller.getFxmlName()));
    fxmlLoader.setController(whiteboardWindowContoller);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneWhiteboard.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
