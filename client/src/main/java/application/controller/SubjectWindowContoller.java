package application.controller;

import application.controller.enums.FollowSubjectResult;
import application.controller.services.FollowSubjectRequestService;
import application.controller.services.MainConnection;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectWindowContoller extends BaseController implements Initializable {

  @FXML
  private AnchorPane coverAnchorPane;

  @FXML
  private Button backToDiscoverButton;

  @FXML
  private Button backToHomeButton;

  @FXML
  private Button followSubjectButton;

  @FXML
  private Button teachSubjectButton;

  @FXML
  private Label followingSubjectLabel;


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
      MainConnection mainConnection, int subject) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = (MainWindowController)
        viewFactory.getWindowControllers().get("MainWindowController");
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
  void backToHomeButtonAction() {
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(0);
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
    TextField textField = new TextField(subject.getName());
    textField.setAlignment(Pos.CENTER);
    textField.setMinHeight(300);
    textField.setMinWidth(1285);
    textField.setEditable(false);
    textField.setMouseTransparent(true);
    textField.setFocusTraversable(false);
    coverAnchorPane.getChildren().add(textField);

    if (mainWindowController.getAccount().getTutorStatus() == 0) {
      teachSubjectButton.setVisible(false);
    }

    updateViews();
  }
}
