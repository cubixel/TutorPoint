package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.enums.TutorRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.SubjectRequestService;
import application.controller.services.TutorRequestService;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private TutorManager tutorManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("RecentWindowController");
  private MainWindowController parentController;

  private SubjectRequestService subjectRequestService;
  private TutorRequestService tutorRequestService;

  private volatile boolean subjectRequestServiceFinished = false;
  @FXML
  private ImageView tutorAvatarOne;

  @FXML
  private Label tutorLabelOne;

  @FXML
  private ImageView tutorAvatarTwo;

  @FXML
  private Label tutorLabelTwo;

  @FXML
  private ImageView tutorAvatarThree;

  @FXML
  private Label tutorLabelThree;

  @FXML
  private ImageView tutorAvatarFour;

  @FXML
  private Label tutorLabelFour;

  @FXML
  private ImageView tutorAvatarFive;

  @FXML
  private Label tutorLabelFive;

  @FXML
  private ScrollBar mainRecentScrollBar;

  @FXML
  private ScrollPane mainRecentScrollPane;

  @FXML
  private AnchorPane mainRecentScrollContent;

  @FXML
  private ScrollPane topSubjectsScrollPane;

  @FXML
  private HBox hboxOne;

  @FXML
  private ScrollPane topTutorsScrollPane;

  @FXML
  private HBox hboxTwo;

  @FXML
  private Label subjectLabelOne;

  @FXML
  private HBox hboxThree;

  @FXML
  private Label subjectLabelTwo;

  @FXML
  private HBox hboxFour;

  @FXML
  private Label subjectLabelThree;

  @FXML
  private HBox hboxFive;

  /**
   * This is the default constructor. RecentWindowController
   * extends the BaseController class. This class is controlling
   * a scene that is nested within the MainWindowController.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param parentController
   *        This is the controller of the scene this class it is nested within
   */
  public RecentWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController parentController) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = parentController.getSubjectManager();
    this.tutorManager = parentController.getTutorManager();
    this.account = parentController.getAccount();
    this.parentController = parentController;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    //Connecting Scroll Bar with Scroll Pane
    mainRecentScrollBar.setOrientation(Orientation.VERTICAL);
    mainRecentScrollBar.minProperty().bind(mainRecentScrollPane.vminProperty());
    mainRecentScrollBar.maxProperty().bind(mainRecentScrollPane.vmaxProperty());
    mainRecentScrollBar.visibleAmountProperty().bind(mainRecentScrollPane.heightProperty().divide(mainRecentScrollContent.heightProperty()));
    mainRecentScrollPane.vvalueProperty().bindBidirectional(mainRecentScrollBar.valueProperty());

    topSubjectsScrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
      if (topSubjectsScrollPane.getHvalue() == 1.0) {
        downloadTopSubjects();
      }
    });

    topTutorsScrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
      if (topTutorsScrollPane.getHvalue() == 1.0) {
        downloadTopTutors();
      }
    });

    downloadTopSubjects();

    // TODO Is there a better way of waiting until another thread has finished?
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    while (!subjectRequestService.isRunning()) {
      // No process just waiting
    }

    downloadTopTutors();
  }

  private void downloadTopSubjects() {
    //TODO Lots of error handling.
    subjectRequestService =
        new SubjectRequestService(getMainConnection(), subjectManager);

    int subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    }

    subjectRequestService.setOnSucceeded(srsEvent -> {
      SubjectRequestResult srsResult = subjectRequestService.getValue();
      subjectRequestServiceFinished = true;

      if (srsResult == SubjectRequestResult.SUBJECT_REQUEST_SUCCESS || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
        for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
          TextField textField = new TextField(subjectManager.getSubject(i).getName());
          textField.setAlignment(Pos.CENTER);
          textField.setMinHeight(130);
          textField.setMinWidth(225);
          textField.setEditable(false);
          textField.setFocusTraversable(false);
          textField.setCursor(Cursor.DEFAULT);
          textField.setOnMouseClicked(e -> {
            try {
              parentController.getDiscoverAnchorPane().getChildren().clear();
              viewFactory.embedSubjectWindow(parentController.getDiscoverAnchorPane(),
                  parentController, subjectManager.getElementNumber(textField.getText()));
            } catch (IOException ioe) {
              ioe.printStackTrace();
            }
            parentController.getPrimaryTabPane().getSelectionModel().select(1);
            e.consume();
          });
          hboxOne.getChildren().add(textField);
        }
      } else {
        System.out.println("Here in mainController srsResult = " + srsResult);
      }
    });
  }

  private void downloadTopTutors() {
    //TODO Lots of error handling.
    tutorRequestService =
        new TutorRequestService(getMainConnection(), tutorManager);

    int tutorsBeforeRequest = tutorManager.getNumberOfTutors();

    if (!tutorRequestService.isRunning()) {
      tutorRequestService.reset();
      tutorRequestService.start();
    }
    tutorRequestService.setOnSucceeded(srsEvent -> {
      TutorRequestResult trsResult = tutorRequestService.getValue();

      if (trsResult == TutorRequestResult.TUTOR_REQUEST_SUCCESS || trsResult == TutorRequestResult.FAILED_BY_NO_MORE_TUTORS) {
        for (int i = tutorsBeforeRequest; i < tutorManager.getNumberOfTutors(); i++) {
          TextField textField = new TextField(tutorManager.getTutor(i).getUsername());
          textField.setAlignment(Pos.CENTER);
          textField.setMinHeight(130);
          textField.setMinWidth(225);
          textField.setEditable(false);
          textField.setMouseTransparent(true);
          textField.setFocusTraversable(false);
          hboxTwo.getChildren().add(textField);
        }
      } else {
        System.out.println("Here in mainController trsResult = " + trsResult);
      }
    });
  }
}
