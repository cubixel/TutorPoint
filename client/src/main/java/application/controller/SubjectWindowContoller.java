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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectWindowContoller extends BaseController implements Initializable {

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


  private SubjectManager subjectManager;
  private Subject subject;
  private AnchorPane discoverWindowAnchorPane;
  private MainWindowController mainWindowController;
  private FollowSubjectRequestService followSubjectRequestService;

  private static final Logger log = LoggerFactory.getLogger("SubjectWindowController");

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection .
   */
  public SubjectWindowContoller(ViewFactory viewFactory, String fxmlName,
      MainWindowController mainWindowController, MainConnection mainConnection, int subject) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    this.subjectManager = mainWindowController.getSubjectManager();
    this.discoverWindowAnchorPane = mainWindowController.getDiscoverAnchorPane();
    this.subject = subjectManager.getSubject(subject);
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
      // TODO This does not look pretty, header image needs scaling of some sort
      String subjectTextNoWhitespace = subject.getName().replaceAll("\\s+","");
      FileInputStream input = new FileInputStream(path
          + subjectTextNoWhitespace + "Header.png");
      // create a image
      Image header = new Image(input);
      headerImageView.setImage(header);
    } catch (FileNotFoundException fnfe) {
      log.warn("No header on server for subject: " + subject.getName());
    }

    if (mainWindowController.getAccount().getTutorStatus() == 0) {
      teachSubjectButton.setVisible(false);
    }

    updateViews();
  }
}
