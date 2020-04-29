package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private TutorManager tutorManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("MainWindowController");

  /**
   * .
   * @param viewFactory .
   * @param fxmlName .
   * @param mainConnection .
   * @param account .
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    tutorManager = new TutorManager();
    this.account = account;
  }

  /**
   * .
   * @param viewFactory .
   * @param fxmlName .
   * @param mainConnection .
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    tutorManager = new TutorManager();
    this.account = null;
  }

  @FXML
  private TabPane navbar;

  @FXML
  private Label usernameLabel;

  @FXML
  private Label tutorStatusLabel;

  @FXML
  private AnchorPane homeWindow;

  @FXML
  private AnchorPane subscriptionsWindow;

  @FXML
  private AnchorPane discoverWindow;

  @FXML
  private AnchorPane profileWindow;

  @FXML
  private AnchorPane streamWindow;

  @FXML
  private Button logOutButton;

  BaseController profileWindowController;


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
    try {
      getMainConnection().sendString("Logout");
    } catch (IOException e) {
      log.error("Failed to tell server to logout", e);
    }
    log.info("Logging Out");
    // Stop current presentation if one exists
    if (getMainConnection().getListener().hasPresentationWindowController()) {
      log.info("Clearing presentation on exit");
      getMainConnection().getListener().getPresentationWindowController().clearPresentation();
      getMainConnection().getListener().setPresentationWindowController(null);
    }
    
    // TODO Remove the users remember me details
    getMainConnection().setUserID(-1);
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showLoginWindow(stage);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    try {
      viewFactory.embedRecentWindow(homeWindow, this);
      viewFactory.embedDiscoverWindow(discoverWindow, this);
      viewFactory.embedProfileWindow(profileWindow, this);
    } catch (IOException e) {
      e.printStackTrace();
    }

    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    //downloadSubjects();

    updateAccountViews();

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
      if (account.getTutorStatus() == 1 && navbar.getTabs().size() < 5) {
        try {
          // TODO It is throwing lots of complaints about size of StreamWindow
          // TODO Keeps adding a new tab every time profile popup is displayed
          AnchorPane anchorPaneStream = new AnchorPane();
          Tab tab = new Tab("Stream");
          tab.setContent(anchorPaneStream);
          navbar.getTabs().add(tab);
          viewFactory.embedStreamWindow(anchorPaneStream, account, account.getUserID(), true);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public TabPane getPrimaryTabPane() {
    return navbar;
  }

  public AnchorPane getDiscoverAnchorPane() {
    return discoverWindow;
  }

  public SubjectManager getSubjectManager() {
    return subjectManager;
  }

  public Account getAccount() {
    return account;
  }

  public TutorManager getTutorManager() {
    return tutorManager;
  }
}
