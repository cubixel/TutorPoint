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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    this.account = null;
  }

  @FXML
  private HBox popUpArea;

  @FXML
  private AnchorPane popUpHolder;

  @FXML
  private TabPane primaryTabPane;

  @FXML
  private TabPane secondaryTabPane;

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
  private Label subjectLabelOne;

  @FXML
  private HBox hboxOne;

  @FXML
  private Label subjectLabelTwo;

  @FXML
  private HBox hboxTwo;

  @FXML
  private Label subjectLabelThree;

  @FXML
  private HBox hboxThree;

  @FXML
  private Label subjectLabelFour;

  @FXML
  private HBox hboxFour;

  @FXML
  private Label subjectLabelFive;

  @FXML
  private HBox hboxFive;

  @FXML
  private Label usernameLabel;

  @FXML
  private ScrollBar scrollBar;

  @FXML
  private Label tutorStatusLabel;

  @FXML
  private AnchorPane anchorPaneProfile;

  BaseController profileWindowController;

  @FXML
  void closePopUp(MouseEvent event) {
    popUpArea.toBack();
    updateAccountViews();
  }

  @FXML
  void openPopUp(MouseEvent event) {
    popUpArea.toFront();
  }

  @FXML
  void mediaPlayerButtonAction() {
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showMediaPlayerWindow(stage);
  }

  @FXML
  void presentationButtonAction() {
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showPresentationWindow(stage);
  }

  @FXML
  void whiteboardButtonAction() {
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showWhiteboardWindow(stage);
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    updateAccountViews();

    try {
      viewFactory.embedProfileWindow(popUpHolder, account);
    } catch (IOException e) {
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

  private void updateAccountViews() {
    if (account != null) {
      usernameLabel.setText(account.getUsername());

      if (account.getTutorStatus() == 0) {
        tutorStatusLabel.setText("Student Account");
      } else {
        tutorStatusLabel.setText("Tutor Account");
      }
    }
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
