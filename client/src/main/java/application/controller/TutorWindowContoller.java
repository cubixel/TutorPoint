package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import application.model.Account;

public class TutorWindowContoller extends BaseController implements Initializable {

  @FXML
  private Button backToDiscoverButton;

  @FXML
  private Label tutorNameLabel;

  @FXML
  private Button backToHomeButton;

  @FXML
  private Button followTutorButton;

  @FXML
  private Circle profilePictureHolder;

  @FXML
  private Label tutorRatingLabel;

  @FXML
  private Slider ratingSlider;

  @FXML
  private Button submitRatingButton;

  private SubjectManager subjectManager;
  // private Account account;
  private Account tutor;
  private AnchorPane parentAnchorPane;
  private MainWindowController parentController;

  private static final Logger log = LoggerFactory.getLogger("SubjectWindowController");

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection .
   */
  public TutorWindowContoller(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController parentController, Account tutor,
      AnchorPane parentAnchorPane) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = parentController.getSubjectManager();
    // this.account = null;
    this.tutor = tutor;
    this.parentAnchorPane = parentAnchorPane;
    this.parentController = parentController;
  }

  @FXML
  void followTutorButton() {
    log.info("Follow Tutor Button Pressed: No Action Taken");
  }

  @FXML
  void submitRatingAction() {
    log.info("Submit Rating Button Pressed: No Action Taken");
  }

  @FXML
  void backToDiscoverButtonAction() {
    try {
      parentAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(parentAnchorPane, parentController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void backToHomeButtonAction() {
    parentController.getPrimaryTabPane().getSelectionModel().select(0);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }
}
