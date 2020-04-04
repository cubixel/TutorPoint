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
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private Account account;

  /**
   * .
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    this.account = account;
  }

  /**
   * .
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   */
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
  private AnchorPane mainRecentScrollContent;

  @FXML
  private Label subjectLabelOne;

  @FXML
  private HBox hboxOne;

  @FXML
  private Label subjectLabelTwo;

  @FXML
  private HBox hboxTwo;

  @FXML
  private HBox hboxThree;

  @FXML
  private HBox hboxFour;

  @FXML
  private Label subjectLabelThree;

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

  @FXML
  private Button logOutButton;

  @FXML
  private ScrollPane topSubjectsScrollPane;

  BaseController profileWindowController;

  @FXML
  void closePopUp() {
    popUpArea.toBack();
    updateAccountViews();
  }

  @FXML
  void openPopUp() {
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

  @FXML
  void logOutButtonAction() {
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showLoginWindow(stage);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    updateAccountViews();

    try {
      viewFactory.embedProfileWindow(popUpHolder, account);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //Connecting Scroll Bar with Scroll Pane
    mainRecentScrollBar.setOrientation(Orientation.VERTICAL);
    mainRecentScrollBar.minProperty().bind(mainRecentScrollPane.vminProperty());
    mainRecentScrollBar.maxProperty().bind(mainRecentScrollPane.vmaxProperty());
    mainRecentScrollBar.visibleAmountProperty().bind(mainRecentScrollPane.heightProperty().divide(mainRecentScrollContent.heightProperty()));
    mainRecentScrollPane.vvalueProperty().bindBidirectional(mainRecentScrollBar.valueProperty());

    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    downloadSubjects();
    String packedSubjectManager = getMainConnection().packageClass(subjectManager);
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

    if (account != null) {
      if (account.getTutorStatus() == 1) {
        try {
          AnchorPane anchorPaneStream = new AnchorPane();
          Tab tab = new Tab("Stream");
          tab.setContent(anchorPaneStream);
          primaryTabPane.getTabs().add(tab);
          viewFactory.embedStreamWindow(anchorPaneStream, account);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void downloadSubjects() {
    //TODO Lots of error handling.
    SubjectRequestService subjectRequestService =
        new SubjectRequestService(getMainConnection(), subjectManager);

    int subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    }
    subjectRequestService.setOnSucceeded(srsEvent -> {
      SubjectRequestResult srsResult = subjectRequestService.getValue();

      if (srsResult == SubjectRequestResult.SUCCESS || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
        FileInputStream input = null;
        for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
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
      } else {
        System.out.println("Here in mainController " + srsResult);
      }
    });
  }
}
