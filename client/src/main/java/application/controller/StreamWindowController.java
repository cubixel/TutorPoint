package application.controller;

import static java.lang.Math.abs;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

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
  private AnchorPane anchorPanePresentation;

  @FXML
  private AnchorPane anchorPaneWhiteboard;

  @FXML
  private AnchorPane pane;

  @FXML
  private AnchorPane paneTwo;

  @FXML
  private Pane resizeXPane;

  @FXML
  private Pane resizeYPane;

  BaseController mediaPlayerController;

  BaseController whiteboardWindowContoller;

  BaseController presentationWindowController;

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


  @FXML
  private void setCursorDefault(MouseEvent event) {
    pane.getScene().getRoot().setCursor(Cursor.DEFAULT);
  }

  @FXML
  private void setCursorHResize(MouseEvent event) {
    pane.getScene().getRoot().setCursor(Cursor.H_RESIZE);
  }

  @FXML
  private void setCursorVResize(MouseEvent event) {
    pane.getScene().getRoot().setCursor(Cursor.V_RESIZE);
  }

  @FXML
  private void redivideHBox(MouseEvent event) {
    if (event.getSceneX() < 650) {
      anchorPaneThree.setPrefWidth(976 - event.getSceneX());
    }
  }

  @FXML
  private void redivideVBox(MouseEvent event) {
    if ((event.getSceneY() < 550) && (event.getSceneY() > 250))
    anchorPaneOne.setPrefHeight(event.getSceneY()-40);
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
    mediaPlayerController = new MediaPlayerController(getViewFactory(),
        "fxml/MediaPlayerWindow.fxml", getMainConnection());
    FXMLLoader fxmlLoader = new FXMLLoader(getViewFactory().getClass()
        .getResource(mediaPlayerController.getFxmlName()));
    fxmlLoader.setController(mediaPlayerController);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneOne.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Second MediaPlayer for testing atm.
    mediaPlayerController = new MediaPlayerController(getViewFactory(),
        "fxml/MediaPlayerWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass()
        .getResource(mediaPlayerController.getFxmlName()));
    fxmlLoader.setController(mediaPlayerController);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneTwo.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // TODO Whiteboards aren't synced, they are currently two separate instances.
    whiteboardWindowContoller = new WhiteboardWindowController(getViewFactory(),
        "fxml/WhiteboardWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass()
        .getResource(whiteboardWindowContoller.getFxmlName()));
    fxmlLoader.setController(whiteboardWindowContoller);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneThree.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    whiteboardWindowContoller = new WhiteboardWindowController(getViewFactory(),
        "fxml/WhiteboardWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass()
        .getResource(whiteboardWindowContoller.getFxmlName()));
    fxmlLoader.setController(whiteboardWindowContoller);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPaneWhiteboard.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }

    presentationWindowController = new PresentationWindowController(getViewFactory(),
        "fxml/PresentationWindow.fxml", getMainConnection());
    fxmlLoader = new FXMLLoader(getViewFactory().getClass()
        .getResource(presentationWindowController.getFxmlName()));
    fxmlLoader.setController(presentationWindowController);
    try {
      pane = (AnchorPane) fxmlLoader.load();
      anchorPanePresentation.getChildren().setAll(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
