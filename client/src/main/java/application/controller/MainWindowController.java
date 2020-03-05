package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.SubjectRenderer;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectRenderer subjectRenderer;
  private SubjectManager subjectManager;

  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
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
  private HBox hBoxOne;

  @FXML
  void profileButtonAction() {

  }


  @FXML
  void tempButton() throws IOException {
    setUpSubjectView();
  }

  @FXML
  void mediaPlayerButtonAction() {
    viewFactory.showMediaPlayerWindow();
    Stage stage = (Stage) hBoxOne.getScene().getWindow();
    viewFactory.closeStage(stage);
  }

  @FXML
  void presentationButtonAction() {
    viewFactory.showPresentationWindow();
    Stage stage = (Stage) hBoxOne.getScene().getWindow();
    viewFactory.closeStage(stage);
  }

  @FXML
  void whiteboardButtonAction() {
    viewFactory.showWhiteboardWindow();
    Stage stage = (Stage) hBoxOne.getScene().getWindow();
    viewFactory.closeStage(stage);
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
    subjectRenderer = new SubjectRenderer(getMainConnection(), hBoxOne, subjectManager);
    subjectRenderer.start();
  }

  //private void setUpTutorView() {
  //}

}
