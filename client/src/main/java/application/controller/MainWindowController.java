package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.SubjectRendererService;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.HBox;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectRendererService subjectRendererService;

  public MainWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @FXML
  private Label profileNameField;

  @FXML
  private ScrollBar scrollBar;

  @FXML
  private HBox HBoxOne;

  @FXML
  void profileButtonAction() {

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

  private void setUpTutorView() {
  }

  private void setUpSubjectView() {
    subjectRendererService = new SubjectRendererService(getMainConnection(), HBoxOne);
  }
}
