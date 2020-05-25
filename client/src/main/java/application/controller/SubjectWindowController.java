package application.controller;

import application.controller.enums.FollowSubjectResult;
import application.controller.services.FollowSubjectRequestService;
import application.controller.services.MainConnection;
import application.model.Subject;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SubjectWindowController displays a subject and its associated
 * information to the user within the Discover tab. This enables the
 * student to follow and rate the subject and see which tutors are
 * teaching it.
 *
 * @author James Gardner
 * @author Oliver Still
 * @see Subject
 * @see SubjectManager
 */
public class SubjectWindowController extends BaseController implements Initializable {

  @FXML
  private ImageView headerImageView;

  @FXML
  private Label followingSubjectLabel;

  @FXML
  private Button followSubjectButton;

  @FXML
  private Button teachSubjectButton;

  @FXML
  private Button backToDiscoverButton;

  @FXML
  private Label subjectLabel;


  private final Subject subject;
  private final AnchorPane discoverWindowAnchorPane;
  private final MainWindowController mainWindowController;
  private final FollowSubjectRequestService followSubjectRequestService;

  private static final Logger log = LoggerFactory.getLogger("SubjectWindowController");


  /**
   * This is the default constructor. SubjectWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainWindowController
   *        Controller for the top level window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param subject
   *        The subject being displayed
   */
  public SubjectWindowController(ViewFactory viewFactory, String fxmlName,
      MainWindowController mainWindowController, MainConnection mainConnection, int subject) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    SubjectManager subjectManager = mainWindowController.getSubjectManager();
    this.discoverWindowAnchorPane = mainWindowController.getDiscoverAnchorPane();
    this.subject = subjectManager.getSubject(subject);
    followSubjectRequestService = new FollowSubjectRequestService(getMainConnection(),
        this.subject.getId(), this.subject.isFollowed());
  }

  /**
   * This is the default constructor. SubjectWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainWindowController
   *        Controller for the top level window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param subject
   *        The subject being displayed
   *
   * @param headerImageView
   *        A JavaFX ImageView for showing the subject header image
   *
   * @param followingSubjectLabel
   *        A JavaFX Label showing the user is following the subject
   *
   * @param followSubjectButton
   *        A JavaFX Button used to follow or un-follow a subject
   *
   * @param teachSubjectButton
   *        A JavaFX Button used to teach or un-teach a subject
   *
   * @param backToDiscoverButton
   *        A JavaFX Button used to return to the discover window
   *
   * @param subjectLabel
   *        A JavaFX Label showing the subject name
   */
  public SubjectWindowController(ViewFactory viewFactory, String fxmlName,
      MainWindowController mainWindowController, MainConnection mainConnection, Subject subject,
      ImageView headerImageView, Label followingSubjectLabel, Button followSubjectButton,
      Button teachSubjectButton, Button backToDiscoverButton, Label subjectLabel) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    this.discoverWindowAnchorPane = mainWindowController.getDiscoverAnchorPane();
    this.subject = subject;
    this.headerImageView = headerImageView;
    this.followingSubjectLabel = followingSubjectLabel;
    this.followSubjectButton = followSubjectButton;
    this.teachSubjectButton = teachSubjectButton;
    this.backToDiscoverButton = backToDiscoverButton;
    this.subjectLabel = subjectLabel;

    followSubjectRequestService = new FollowSubjectRequestService(getMainConnection(),
        this.subject.getId(), this.subject.isFollowed());
  }

  @FXML
  void backToDiscoverButtonAction() {
    try {
      discoverWindowAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(discoverWindowAnchorPane, mainWindowController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void followSubjectButton() {
    followSubjectRequestService.setFollowing(subject.isFollowed());

    if (!followSubjectRequestService.isRunning()) {
      followSubjectRequestService.reset();
      followSubjectRequestService.start();
    }

    followSubjectRequestService.setOnSucceeded(trsEvent -> {
      FollowSubjectResult fsrsResult = followSubjectRequestService.getValue();
      switch (fsrsResult) {
        case FOLLOW_SUBJECT_RESULT_SUCCESS:
          subject.setFollowed(!subject.isFollowed());
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
  void teachSubjectButton() {
    // TODO Not yet implemented
    log.info("Teach Subject Button pressed but no action taken, not yet implemented");
  }

  private void updateViews() {
    if (subject.isFollowed()) {
      followingSubjectLabel.setText("You are following this subject");
      followSubjectButton.setText("Unfollow Subject");
    } else {
      followingSubjectLabel.setText("You are not following this subject");
      followSubjectButton.setText("Follow Subject");
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "subjects"
        + File.separator + "headers" + File.separator;

    try {
      String subjectTextNoWhitespace = subject.getName().replaceAll("\\s+","");
      FileInputStream input = new FileInputStream(path
          + subjectTextNoWhitespace + "Header.png");
      // create a image
      Image header = new Image(input);
      headerImageView.setImage(header);
      headerImageView.setPreserveRatio(true);
      headerImageView.setViewport(new Rectangle2D(0,
          (header.getHeight() / 2) - 130, header.getWidth(), 390));

      subjectLabel.setText(subject.getName());
    } catch (FileNotFoundException fnfe) {
      log.warn("No header on server for subject: " + subject.getName());
    }

    if (mainWindowController.getAccount().getTutorStatus() == 0) {
      teachSubjectButton.setVisible(false);
    }
    updateViews();
  }
}