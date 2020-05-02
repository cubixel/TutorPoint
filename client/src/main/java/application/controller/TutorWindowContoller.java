package application.controller;

import application.controller.enums.FollowTutorResult;
import application.controller.enums.TutorRequestResult;
import application.controller.services.FollowTutorRequestService;
import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.controller.services.TutorRequestService;
import application.model.Account;
import application.model.Tutor;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private Label followingTutorLabel;

  @FXML
  private Slider ratingSlider;

  @FXML
  private Button submitRatingButton;

  private SubjectManager subjectManager;
  private final Account account;
  private Tutor tutor;
  private AnchorPane parentAnchorPane;
  private MainWindowController parentController;
  private FollowTutorRequestService followTutorRequestService;

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
      MainConnection mainConnection, MainWindowController mainWindowController, Tutor tutor,
      AnchorPane parentAnchorPane) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = mainWindowController.getSubjectManager();
    this.account = mainWindowController.getAccount();
    this.tutor = tutor;
    this.parentAnchorPane = parentAnchorPane;
    this.parentController = mainWindowController;
  }

  private void checkSafeToDownload() {
    try {
      //noinspection StatementWithEmptyBody
      while (!followTutorRequestService.isFinished()) {
      }
    } catch (NullPointerException e) {
      log.info("First Follow Request");
    }
  }

  @FXML
  void followTutorButton() {
    checkSafeToDownload();

    followTutorRequestService =
        new FollowTutorRequestService(getMainConnection(), tutor.getUserID(), tutor.isFollowed());

    if (!followTutorRequestService.isRunning()) {
      followTutorRequestService.reset();
      followTutorRequestService.start();
    }

    followTutorRequestService.setOnSucceeded(trsEvent -> {
      FollowTutorResult ftsResult = followTutorRequestService.getValue();
      switch (ftsResult) {
        case FOLLOW_TUTOR_RESULT_SUCCESS:
          tutor.setFollowed(!tutor.isFollowed());
          updateViews();
          break;
        case FAILED_BY_NETWORK:
          log.error("FAILED_BY_NETWORK");
          break;
        case FAILED_BY_DATABASE_ERROR:
          log.error("FAILED_BY_DATABASE_ERROR");
          break;
        default:
          log.error("FAILED_BY_UNKNOWN_ERROR");
          break;
      }

    });
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
    getMainConnection().getListener().addTutorWindowController(this);
    downloadProfilePicture();
    updateViews();
  }

  private void updateViews() {
    if (tutor.isFollowed()) {
      followingTutorLabel.setText("You are following this tutor");
      followTutorButton.setText("Unfollow Tutor");
    } else {
      followingTutorLabel.setText("You are not following this tutor");
      followTutorButton.setText("Follow Tutor");
    }

    tutorNameLabel.setText(tutor.getUsername());
    tutorRatingLabel.setText(String.valueOf(Math.round(tutor.getRating())));

  }

  private void downloadProfilePicture() {
    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded"
        + File.separator + "profilePictures" + File.separator;

    try {
      FileInputStream input = new FileInputStream(path + "user"
          + tutor.getUserID() + "profilePicture.png");

      log.debug(path + "user" + tutor.getUserID() + "profilePicture.png");

      Image profileImage = new Image(input);
      tutor.setProfilePicture(profileImage);
      ImagePattern imagePattern = new ImagePattern(tutor.getProfilePicture());
      profilePictureHolder.setFill(imagePattern);

    } catch (FileNotFoundException fnfe) {
      log.warn("Tutor " + tutor.getUsername() + " has no profile picture");
    }
  }
}
