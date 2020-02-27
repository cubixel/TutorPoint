package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.SubjectRendererService;
import application.model.managers.SubjectManager;
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

  private SubjectRendererService subjectRendererService;

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
    subjectRendererService =new SubjectRendererService(getMainConnection(), HBoxOne);
    subjectRendererService.start();
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
    primaryTabPane.getStyleClass().add("blue-tab-pane");
    secondaryTabPane.getStyleClass().add("white-tab-pane");
  }

  private void setUpTutorView() {
  }

  private void setUpSubjectView() {
    subjectRendererService = new SubjectRendererService(getMainConnection(), HBoxOne);
  }
}
