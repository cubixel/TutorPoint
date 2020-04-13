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

  private SubjectManager subjectManagerTopSubjects;
  private SubjectManager subjectManagerRecommendationsOne;
  private SubjectManager subjectManagerRecommendationsTwo;
  private SubjectManager subjectManagerRecommendationsThree;
  private TutorManager tutorManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("RecentWindowController");
  private MainWindowController parentController;

  private SubjectRequestService subjectRequestService;
  private TutorRequestService tutorRequestService;

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
    this.subjectManagerTopSubjects = parentController.getSubjectManager();
    this.tutorManager = parentController.getTutorManager();
    this.account = parentController.getAccount();
    this.parentController = parentController;

    this.subjectManagerRecommendationsOne = new SubjectManager();
    this.subjectManagerRecommendationsTwo = new SubjectManager();
    this.subjectManagerRecommendationsThree = new SubjectManager();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
        //Connecting Scroll Bar with Scroll Pane
    mainRecentScrollBar.setOrientation(Orientation.VERTICAL);
    mainRecentScrollBar.minProperty().bind(mainRecentScrollPane.vminProperty());
    mainRecentScrollBar.maxProperty().bind(mainRecentScrollPane.vmaxProperty());
    mainRecentScrollBar.visibleAmountProperty().bind(mainRecentScrollPane.heightProperty()
        .divide(mainRecentScrollContent.heightProperty()));
    mainRecentScrollPane.vvalueProperty().bindBidirectional(mainRecentScrollBar.valueProperty());

    topSubjectsScrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
      if (topSubjectsScrollPane.getHvalue() == 1.0) {
        downloadSubjects(hboxOne, subjectManagerTopSubjects, null);
      }
    });

    topTutorsScrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
      if (topTutorsScrollPane.getHvalue() == 1.0) {
        downloadTopTutors();
      }
    });

    downloadSubjects(hboxOne, subjectManagerTopSubjects, null);

    //noinspection StatementWithEmptyBody
    while (!subjectRequestService.isFinished()) {
      /* This is used due to race conditions with
       * the JavaFX Service threads accessing the
       * MainConnections DataInputStream. The
       * Thread have a Boolean 'finished' that
       * is initialised as 'false'. This is set
       * true when the thread has completed. */
    }

    downloadTopTutors();

    //noinspection StatementWithEmptyBody
    while (!tutorRequestService.isFinished()) {
    }

    setUpFollowedSubjects();

  }

  private void downloadSubjects(HBox hbox, SubjectManager subjectManager, String subject) {
    subjectRequestService = new SubjectRequestService(getMainConnection(), subjectManager, subject);

    int subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    } else {
      log.debug("SubjectRequestService is currently running");
    }

    subjectRequestService.setOnSucceeded(srsEvent -> {
      // TODO This seems to only fire at the end of initialise, which means all values
      // except the last are null. Very odd.
      // Added a new getter get result and this has fixed it. Not sure why getValue was not working.
      SubjectRequestResult srsResult = subjectRequestService.getResult();

      log.debug("srsResult = " + srsResult + " and Subject = " + subject);

      if (srsResult == SubjectRequestResult.SUBJECT_REQUEST_SUCCESS
          || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
        hbox.getChildren().clear();
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
              log.debug(textField.getText());
            } catch (IOException ioe) {
              log.error("Could not embed the Subject Window", ioe);
            }
            parentController.getPrimaryTabPane().getSelectionModel().select(1);
            e.consume();
          });
          hbox.getChildren().add(textField);
        }
      } else {
        log.info("SubjectRequestService Result = " + srsResult);
      }
    });
  }

  private void downloadTopTutors() {
    tutorRequestService =
        new TutorRequestService(getMainConnection(), tutorManager);

    int tutorsBeforeRequest = tutorManager.getNumberOfTutors();

    if (!tutorRequestService.isRunning()) {
      tutorRequestService.reset();
      tutorRequestService.start();
    }

    tutorRequestService.setOnSucceeded(trsEvent -> {
      TutorRequestResult trsResult = tutorRequestService.getValue();

      if (trsResult == TutorRequestResult.TUTOR_REQUEST_SUCCESS
          || trsResult == TutorRequestResult.FAILED_BY_NO_MORE_TUTORS) {
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
        log.debug("TutorRequestService Result = " + trsResult);
      }
    });
  }

  private void createLink() {

  }

  private void setUpFollowedSubjects() {
    // TODO
    int numberOfFollowedSubjects = account.getFollowedSubjects().size();

    switch (numberOfFollowedSubjects) {
      case 1:
        subjectLabelOne.setText(account.getFollowedSubjects().get(0));
        break;
      case 2:
        subjectLabelOne.setText(account.getFollowedSubjects().get(0));
        subjectLabelTwo.setText(account.getFollowedSubjects().get(1));
        break;
      default:
        subjectLabelOne.setText(account.getFollowedSubjects().get(0));
        subjectLabelTwo.setText(account.getFollowedSubjects().get(1));
        subjectLabelThree.setText(account.getFollowedSubjects().get(2));
        while (!subjectRequestService.isFinished()) {

        }

        downloadSubjects(hboxThree, subjectManagerRecommendationsOne, account.getFollowedSubjects().get(0));

        while (!subjectRequestService.isFinished()) {

        }
        downloadSubjects(hboxFour, subjectManagerRecommendationsTwo, account.getFollowedSubjects().get(1));
        while (!subjectRequestService.isFinished()) {

        }

        downloadSubjects(hboxFive, subjectManagerRecommendationsThree, account.getFollowedSubjects().get(2));
        break;
    }
  }
}
