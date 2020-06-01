package application.controller;

import application.controller.enums.FollowTutorResult;
import application.controller.services.FollowTutorRequestService;
import application.controller.services.MainConnection;
import application.model.Tutor;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TutorWindowController contains the control methods
 * for the FXML TutorWindow page. It is used to display a
 * tutors profile and the users relation with that tutor.
 *
 * @author James Gardner
 * @see Tutor
 * @see FollowTutorRequestService
 */
public class TutorWindowController extends BaseController implements Initializable {

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

  @FXML
  private HBox subjectsHBox;

  private final Tutor tutor;
  private final AnchorPane parentAnchorPane;
  private final MainWindowController parentController;
  private final FollowTutorRequestService followTutorRequestService;

  private static final Logger log = LoggerFactory.getLogger("TutorWindowController");

  /**
   * This is the default constructor. TutorWindowController
   * extends the BaseController class.
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
   * @param mainWindowController
   *        Controller for the top level window
   *
   * @param tutor
   *        The tutor account being displayed
   *
   * @param parentAnchorPane
   *        A JavaFX AnchorPane of the discover tab
   */
  public TutorWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController, Tutor tutor,
      AnchorPane parentAnchorPane) {
    super(viewFactory, fxmlName, mainConnection);
    this.tutor = tutor;
    this.parentAnchorPane = parentAnchorPane;
    this.parentController = mainWindowController;
    followTutorRequestService =
        new FollowTutorRequestService(getMainConnection(), tutor.getUserID(), tutor.isFollowed());
  }

  /**
   * This is the constructor used for testing. TutorWindowController
   * extends the BaseController class.
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
   * @param mainWindowController
   *        Controller for the top level window
   *
   * @param tutor
   *        The tutor account being displayed
   *
   * @param parentAnchorPane
   *        A JavaFX AnchorPane of the discover tab
   *
   * @param backToDiscoverButton
   *        A JavaFX Button used to return to the discover window
   *
   * @param tutorNameLabel
   *        A JavaFX Label showing the tutors name
   *
   * @param backToHomeButton
   *        A JavaFX Button used to return to the home window
   *
   * @param followTutorButton
   *        A JavaFX Button used to follow or un-follow a tutor
   *
   * @param profilePictureHolder
   *        A JavaFX Circle used to contain the Tutors profile picture
   *
   * @param tutorRatingLabel
   *        A JavaFX Label showing the tutors total average rating
   *
   * @param followingTutorLabel
   *        A JavaFX Label showing the user is following the tutor
   *
   * @param ratingSlider
   *        A JavaFX Slider used to ser the users rating of the tutor
   *
   * @param submitRatingButton
   *        A JavaFX Button of which its action submits the rating
   *
   * @param subjectsHBox
   *        An JavaFX HBox for displaying the subjects the tutor teaches
   */
  public TutorWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController, Tutor tutor,
      AnchorPane parentAnchorPane, Button backToDiscoverButton, Label tutorNameLabel,
      Button backToHomeButton, Button followTutorButton, Circle profilePictureHolder,
      Label tutorRatingLabel, Label followingTutorLabel, Slider ratingSlider,
      Button submitRatingButton, HBox subjectsHBox) {
    super(viewFactory, fxmlName, mainConnection);
    this.tutor = tutor;
    this.parentAnchorPane = parentAnchorPane;
    this.parentController = mainWindowController;
    this.backToDiscoverButton = backToDiscoverButton;
    this.tutorNameLabel = tutorNameLabel;
    this.backToHomeButton = backToHomeButton;
    this.followTutorButton = followTutorButton;
    this.profilePictureHolder = profilePictureHolder;
    this.tutorRatingLabel = tutorRatingLabel;
    this.followingTutorLabel = followingTutorLabel;
    this.ratingSlider = ratingSlider;
    this.submitRatingButton = submitRatingButton;
    this.subjectsHBox = subjectsHBox;

    followTutorRequestService =
        new FollowTutorRequestService(getMainConnection(), tutor.getUserID(), tutor.isFollowed());
  }

  @FXML
  void followTutorButton() {
    followTutorRequestService.setFollowing(tutor.isFollowed());

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
  }

  @FXML
  void submitRatingAction() {
    // TODO not yet implemented
    log.info("Submit Rating Button Pressed: No Action Taken");
  }

  @FXML
  void backToDiscoverButtonAction() {
    try {
      parentAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(parentAnchorPane, parentController);
    } catch (IOException e) {
      log.error("Could not change view to discover window", e);
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

  private void downloadTopSubjects() {
    // TODO Send request to server similar implementation to HomeWindowController
  }
}
