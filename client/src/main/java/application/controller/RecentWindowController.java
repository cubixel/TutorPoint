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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private TutorManager tutorManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("Client Logger");
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
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection
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


    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    downloadTopSubjects();

    // TODO Find a better way of waiting until another thread has finished
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    while (!subjectRequestService.isRunning()) {
      // No process
    }

    downloadTopTutors();
  }

  @FXML
  void hBoxMouserClickedAction(MouseEvent event) {
    int widthOfImages = 225;

    // TODO fix for widths of variable size
    int element = (int) event.getX()/widthOfImages;
    try {
      parentController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory.embedSubjectWindow(parentController.getDiscoverAnchorPane(), parentController, element);
    } catch (IOException e) {
      e.printStackTrace();
    }
    parentController.getPrimaryTabPane().getSelectionModel().select(1);
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

      if (srsResult == SubjectRequestResult.SUCCESS || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
        for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
          TextField textField = new TextField(subjectManager.getSubject(i).getName());
          textField.setAlignment(Pos.CENTER);
          textField.setMinHeight(130);
          textField.setMinWidth(225);
          textField.setEditable(false);
          textField.setMouseTransparent(true);
          textField.setFocusTraversable(false);
          hboxOne.getChildren().add(textField);
        }
      } else {
        System.out.println("Here in mainController " + srsResult);
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

      if (trsResult == TutorRequestResult.SUCCESS || trsResult == TutorRequestResult.FAILED_BY_NO_MORE_TUTORS) {
        for (int i = tutorsBeforeRequest; i < tutorManager.getNumberOfTutors(); i++) {
          log.debug("RecentWindowController: downloadTopTutors() ID = " + tutorManager.getTutor(i).getUserID());
          log.debug("RecentWindowController: downloadTopTutors() Useranme = " + tutorManager.getTutor(i).getUsername());
          log.debug("RecentWindowController: downloadTopTutors() Rating = " + tutorManager.getTutor(i).getRating());
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
        System.out.println("Here in mainController " + trsResult);
      }
    });
  }
}
