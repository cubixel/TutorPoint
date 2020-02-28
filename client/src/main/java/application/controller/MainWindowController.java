package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.SubjectRenderer;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectRenderer subjectRenderer;

  public MainWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @FXML
  private TabPane primaryTabPane;

  @FXML
  private TabPane secondaryTabPane;

  @FXML
  private Label profileNameField;

  @FXML
  private ScrollBar scrollBar;

  @FXML
  private HBox HBoxOne;

  @FXML
  void profileButtonAction() {

  }


  @FXML
  void tempButton() throws IOException {
    subjectRenderer = new SubjectRenderer(getMainConnection(), HBoxOne);
    subjectRenderer.start();
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    //setUpSubjectView();
    //setUpTutorView();
  }

  private void setUpSubjectView() {
    subjectRenderer = new SubjectRenderer(getMainConnection(), HBoxOne);
  }

  private void setUpTutorView() {
  }

}
