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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MainWindowController is the top level window once
 * the user has logged in. It contains all other windows
 * within it allowing the user to swap between the windows
 * as tabs.
 *
 * @author James Gardner
 * @author Stijn Marynissen
 * @author Oliver Still
 * @author Daniel Bishop
 * @author Eric Walker
 */
public class MainWindowController extends BaseController implements Initializable {

  private final SubjectManager subjectManager;
  private final TutorManager tutorManager;
  private final Account account;

  private static final Logger log = LoggerFactory.getLogger("MainWindowController");

  @FXML
  private TabPane navbar;

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
  private ButtonBar buttonbar;

  @FXML
  private Button logOutButton;

  @FXML
  void logOutButtonAction() {
    logout();
  }

  /**
   * This is the default constructor. MainWindowController
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
   * @param account
   *        The users Account post successful login
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    subjectManager = new SubjectManager();
    tutorManager = new TutorManager();
    this.account = account;
  }

  /**
   * This is the constructor used for testing. MainWindowController
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
   * @param subjectManager
   *        A SubjectManager for containing the top subjects
   *
   * @param tutorManager
   *        A TutorManager for containing the top tutors
   *
   * @param account
   *        The users Account post successful login
   *
   * @param navbar
   *        A JavaFX TabPane used to swap between other windows
   *
   * @param homeWindow
   *        A JavaFX AnchorPane showing the HomeWindowController
   *
   * @param subscriptionsWindow
   *        A JavaFX AnchorPane showing the SubscriptionsWindowController
   *
   * @param discoverWindow
   *        A JavaFX AnchorPane showing the DiscoverWindowController
   *
   * @param profileWindow
   *        A JavaFX AnchorPane showing the ProfileWindowController
   *
   * @param streamWindow
   *        A JavaFX AnchorPane showing the StreamWindowController
   *
   * @param buttonbar
   *        A JavaFX ButtonBar containing the LogOut Button
   *
   * @param logOutButton
   *        A JavaFX Button used to logout the user
   */
  public MainWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, SubjectManager subjectManager, TutorManager tutorManager,
      Account account, TabPane navbar, AnchorPane homeWindow, AnchorPane subscriptionsWindow,
      AnchorPane discoverWindow, AnchorPane profileWindow, AnchorPane streamWindow,
      ButtonBar buttonbar, Button logOutButton) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = subjectManager;
    this.tutorManager = tutorManager;
    this.account = account;
    this.navbar = navbar;
    this.homeWindow = homeWindow;
    this.subscriptionsWindow = subscriptionsWindow;
    this.discoverWindow = discoverWindow;
    this.profileWindow = profileWindow;
    this.streamWindow = streamWindow;
    this.buttonbar = buttonbar;
    this.logOutButton = logOutButton;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    getMainConnection().setMainWindow(this);

    try {
      viewFactory.embedHomeWindow(homeWindow, this);
      viewFactory.embedDiscoverWindow(discoverWindow, this);
      viewFactory.embedProfileWindow(profileWindow, this);
      viewFactory.embedSubscriptionsWindow(subscriptionsWindow, this);
    } catch (IOException e) {
      log.error("Could not initialise embedded windows", e);
    }

    assert account != null;
    if (account.getTutorStatus() == 1) {
      try {
        viewFactory.embedStreamWindow(streamWindow, account, account.getUserID(), true);
      } catch (IOException e) {
        log.error("Failed to embed Stream Window Controller", e);
      }
    } else {
      navbar.getTabs().remove(4);
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

  public TabPane getNavbar() {
    return navbar;
  }

  /**
   * Perform logoff actions.
   */
  public void logout() {
    try {
      getMainConnection().sendString("Logout");
    } catch (IOException e) {
      log.error("Failed to tell server to logout", e);
    }
    log.info("Logging Out");
    // Stop current presentation if one exists
    if (getMainConnection().getListener().getPresentationWindowController() != null) {
      log.info("Clearing presentation on exit");
      getMainConnection().getListener().getPresentationWindowController().clearPresentation();
      getMainConnection().getListener().clearPresentationWindowController();
    }

    //End webcam stream
    log.info("Checking webcam...");
    if (getMainConnection().getListener().getWebcamWindowController() != null) {
      log.info("Stopping webcam");
      getMainConnection().getListener().getWebcamWindowController().endStream();
    }

    // TODO Any other modules need closing down correctly similar to the presentation above
    
    // TODO Remove the users remember me details
    getMainConnection().setUserID(-1);
    getMainConnection().setMainWindow(null);
    Stage stage = (Stage) navbar.getScene().getWindow();
    viewFactory.showLoginWindow(stage);
  }
}