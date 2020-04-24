package application.controller;

import application.controller.enums.LiveTutorRequestResult;
import application.controller.services.LiveTutorRequestService;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private TutorManager tutorManager;
  private TutorManager liveTutorManager;
  private Account account;

  private LiveTutorRequestService liveTutorRequestService;

  private static final Logger log = LoggerFactory.getLogger("MainWindowController");

  /**
   * .
   *
   * @param viewFactory    .
   * @param fxmlName       .
   * @param mainConnection .
   * @param account        .
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    tutorManager = new TutorManager();
    this.account = account;
    this.liveTutorManager = new TutorManager();
  }

  /**
   * .
   *
   * @param viewFactory    .
   * @param fxmlName       .
   * @param mainConnection .
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    tutorManager = new TutorManager();
    this.liveTutorManager = new TutorManager();
    this.account = null;
  }

  @FXML
  private TabPane navbar;

  @FXML
  private Tab streamTab;

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

  @FXML
  private Pane profilePane;

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
    log.info("Loging Out");
    // TODO Remove the users remember me details
    Stage stage = (Stage) usernameLabel.getScene().getWindow();
    viewFactory.showLoginWindow(stage);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    try {
      viewFactory.embedRecentWindow(homeWindow, this);
      viewFactory.embedDiscoverWindow(discoverWindow, this);
      viewFactory.embedProfileWindow(profileWindow, this);
      viewFactory.embedSubscriptionsWindow(subscriptionsWindow);

      /*profilePane.setOnMouseEntered(e -> {
        Thread.currentThread().interrupt();
        new Thread(() -> {
          while (profilePane.getWidth() < 200) {
            if (Thread.interrupted()) {
              System.out.println("Left Stopped");
              break;
            }
            profilePane.setPrefWidth(profilePane.getWidth() + 10);
          }
          System.out.println("Left Done");
        }).start();
      });

      profilePane.setOnMouseExited(e -> {
        Thread.currentThread().interrupt();
        new Thread(() -> {
          while (profilePane.getWidth() > 20) {
            try {
              if (Thread.interrupted()) {
                System.out.println("Right Stopped");
                throw new InterruptedException();
              }
              profilePane.setPrefWidth(profilePane.getWidth() - 10);
            } catch (InterruptedException ie)  {
              break;
            }
          }
          System.out.println("Right Done");
        }).start();
      });*/

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
        navbar.getTabs().remove(streamTab);
      } else {
        tutorStatusLabel.setText("Tutor Account");
        try {
          viewFactory.embedStreamWindow(streamWindow, account, account.getUserID(), true);
        } catch (IOException e) {
          log.error("Failed to embed Stream Window Controller", e);
        }
      }
    }
  }

  // TODO Integrate this into the live tutors vbox
//  private void downloadLiveTutors() {
//    liveTutorRequestService =
//        new LiveTutorRequestService(getMainConnection(), liveTutorManager);
//
//    int tutorsBeforeRequest = liveTutorManager.getNumberOfTutors();
//
//    if (!liveTutorRequestService.isRunning()) {
//      liveTutorRequestService.reset();
//      liveTutorRequestService.start();
//    }
//
//    liveTutorRequestService.setOnSucceeded(trsEvent -> {
//      LiveTutorRequestResult trsResult = liveTutorRequestService.getValue();
//
//      if (tutorsBeforeRequest != liveTutorManager.getNumberOfTutors()) {
//        hboxThree.getChildren().clear();
//        if (trsResult == LiveTutorRequestResult.LIVE_TUTOR_REQUEST_SUCCESS
//            || trsResult == LiveTutorRequestResult.NO_MORE_LIVE_TUTORS) {
//          AnchorPane[] linkHolder = createLinkHolders(hboxThree);
//
//          ParallelTransition parallelTransition = new ParallelTransition();
//
//          for (int i = tutorsBeforeRequest; i < liveTutorManager.getNumberOfTutors(); i++) {
//            String tutorName = liveTutorManager.getTutor(i).getUsername();
//            int tutorID = liveTutorManager.getTutor(i).getUserID();
//            displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
//            linkHolder[i % 5].setOnMouseClicked(e -> setStreamWindow(tutorID) );
//          }
//
//          parallelTransition.setCycleCount(1);
//          parallelTransition.play();
//
//        } else {
//          log.debug("LiveTutorRequestService Result = " + trsResult);
//        }
//      }
//    });
//  }

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

  public Tab getStreamTab() {
    return streamTab;
  }

  /*Task<Void> moveSidePaneLeft = new Task<Void>() {
    @Override
    protected Void call() throws Exception {
      while (profilePane.getWidth() < 200) {
        if (Thread.currentThread().isInterrupted()) {
          System.out.println("Left Stopped");
          return null;
        }
        profilePane.setPrefWidth(profilePane.getWidth() + 10);
      }
      System.out.println("Left Done");
      return null;
    }
  };

  Task<Void> moveSidePaneRight = new Task<Void>() {
    @Override
    protected Void call() throws Exception {
      while (profilePane.getWidth() > 20) {
        if (Thread.currentThread().isInterrupted()) {
          System.out.println("Right Stopped");
          return null;
        }
        profilePane.setPrefWidth(profilePane.getWidth() - 10);
      }
      System.out.println("Right Done");
      return null;
    }
  };*/
}
