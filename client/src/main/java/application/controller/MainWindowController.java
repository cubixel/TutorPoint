package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.SubjectRequestService;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private Account account;

  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    this.account = account;
  }

  @FXML
  private TabPane primaryTabPane;

  @FXML
  private TabPane secondaryTabPane;

  @FXML
  private Label profileNameLabel;

  @FXML
  private ScrollBar scrollBar;

  @FXML
  private Label tutorStatusLabel;

  @FXML
  private HBox hboxOne;

  @FXML
  private AnchorPane anchorPaneProfile;

  BaseController profileWindowController;

  @FXML
  void profileButtonAction() {

  }

  @FXML
  void mediaPlayerButtonAction() {
    Stage stage = (Stage) profileNameLabel.getScene().getWindow();
    viewFactory.showMediaPlayerWindow(stage);
  }

  @FXML
  void presentationButtonAction() {
    Stage stage = (Stage) profileNameLabel.getScene().getWindow();
    viewFactory.showPresentationWindow(stage);
  }

  @FXML
  void whiteboardButtonAction() {
    Stage stage = (Stage) profileNameLabel.getScene().getWindow();
    viewFactory.showWhiteboardWindow(stage);
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (account != null) {
      profileNameLabel.setText(account.getUsername());

      if (account.getTutorStatus() == 0) {
        tutorStatusLabel.setText("Student Account");
      } else {
        tutorStatusLabel.setText("Tutor Account");
      }
    }

    try {
      viewFactory.embedProfileWindow(anchorPaneProfile, account);
    } catch (IOException e) {
      System.out.println("Failed to Setup Profile Tab");
      e.printStackTrace();
    }

    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    downloadSubjects();
  }

  private void downloadSubjects() {
    //TODO Lots of error handling.
    SubjectRequestService subjectRequestService =
        new SubjectRequestService(getMainConnection(), subjectManager);

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    }
    subjectRequestService.setOnSucceeded(srsEvent -> {
      SubjectRequestResult srsResult = subjectRequestService.getValue();
      switch (srsResult) {
        case SUCCESS:
          FileInputStream input = null;
          for (int i = 0; i < subjectManager.getNumberOfSubjects(); i++) {
            try {
              input = new FileInputStream(subjectManager.getSubject(i).getThumbnailPath());
              Image image = new Image(input);
              ImageView imageView = new ImageView(image);
              imageView.setFitHeight(130);
              imageView.setFitWidth(225);
              hboxOne.getChildren().add(imageView);
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            }
          }
          break;
        case FAILED_BY_NETWORK:
          System.out.println("FAILED_BY_NETWORK");
          break;
        case FAILED_BY_NO_MORE_SUBJECTS:
          System.out.println("FAILED_BY_NO_MORE_SUBJECTS");
          break;
        default:
          System.out.println("UNKNOWN ERROR");
      }
    });
  }
}
