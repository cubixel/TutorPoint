package application.view;

import application.controller.BaseController;
import application.controller.DiscoverWindowController;
import application.controller.LoginWindowController;
import application.controller.MainWindowController;
import application.controller.MediaPlayerController;
import application.controller.PresentationWindowController;
import application.controller.ProfileWindowController;
import application.controller.HomeWindowController;
import application.controller.RegisterWindowController;
import application.controller.StreamWindowController;
import application.controller.SubjectWindowContoller;
import application.controller.SubscriptionsWindowController;
import application.controller.TextChatWindowController;
import application.controller.WebcamWindowController;
import application.controller.WhiteboardWindowController;
import application.controller.services.MainConnection;
import application.model.Account;
import java.io.IOException;
import java.util.HashMap;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to generate and the views that
 * the user will see. It contains the methods used to generate
 * a 'Controller' to be applied to the supplied Stage for
 * particular FXMLScene. It also generates Controllers for
 * embedded FXML Scenes.
 *
 * @author James Gardner
 * @see    ViewInitialiser
 * @see    BaseController
 */
public class ViewFactory {

  private MainConnection mainConnection;
  private ViewInitialiser viewInitialiser;
  private HashMap<String, BaseController> windowControllers;

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("ViewFactory");

  /**
   * Initialises a newly created {@code ViewFactory} object. Needs
   * access to the main client-server connection in order to
   * distribute this to the controllers. This constructor makes
   * creates a new {@code ViewInitialiser} and then calls the
   * other constructor created for testing purposes.
   *
   * @param mainConnection
   *        This is the main connection to the server, established on startup
   *
   */
  public ViewFactory(MainConnection mainConnection) {
    /* Makes a call to the other constructor that has been created for testing. */
    this(mainConnection, new ViewInitialiser());
  }

  /**
   * This constructor is used for testing the {@code ViewFactory}
   * by providing access to all fields so that Mockito
   * versions of those objects can be provided.
   *
   * @param mainConnection
   *        This is the main connection to the server, established on startup
   *
   * @param viewInitialiser
   *        The ViewInitialiser to connect Controllers with Scenes
   *
   */
  public ViewFactory(MainConnection mainConnection, ViewInitialiser viewInitialiser) {
    this.mainConnection = mainConnection;
    this.viewInitialiser = viewInitialiser;
    windowControllers = new HashMap<String, BaseController>();
  }

  /**
   * Creates a LoginWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showLoginWindow(Stage stage) {
    /* Each window needs a controller specific to it. This
     * is creating a new LoginWindowController using the
     * Abstract class BaseController. The LoginWindow.fxml
     * is passed in as an argument. */
    BaseController loginWindowController =
        new LoginWindowController(this, "fxml/LoginWindow.fxml", mainConnection);

    stage.setResizable(false);
    viewInitialiser.initialiseStage(loginWindowController, stage);
    windowControllers.put("LoginWindowController", loginWindowController);
    log.info("Login Window Setup");
  }

  /**
   * Creates a MainWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showMainWindow(Stage stage, Account account) {
    /* The MainWindowController takes the MainWindow.fxml
     * as its argument. These fxml files must be placed in
     * the correct folder: resources -> view -> fxml */
    BaseController mainWindowController =
        new MainWindowController(this, "fxml/MainWindow.fxml", mainConnection, account);
    stage.setResizable(true);
    viewInitialiser.initialiseStage(mainWindowController, stage);
    windowControllers.put("MainWindowController", mainWindowController);
    log.info("Main Window Setup");
  }

  /**
   * Creates a RegisterWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showRegisterWindow(Stage stage) {
    BaseController registerWindowController =
        new RegisterWindowController(this, "fxml/RegisterWindow.fxml", mainConnection);

    stage.setResizable(false);
    viewInitialiser.initialiseStage(registerWindowController, stage);
    windowControllers.put("RegisterWindowController", registerWindowController);
    log.info("Registration Window Setup");
  }

  /**
   * Creates a WhiteboardWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showWhiteboardWindow(Stage stage) {
    BaseController whiteboardWindowController =
        new WhiteboardWindowController(this, "fxml/stream/WhiteboardWindow.fxml",
            mainConnection, "userId-000", "session-000");
    viewInitialiser.initialiseStage(whiteboardWindowController, stage);
    windowControllers.put("WhiteboardWindowController", whiteboardWindowController);
  }


  /**
   * Creates a PresentationWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showPresentationWindow(Stage stage) {
    BaseController presentationWindowController =
        new PresentationWindowController(this, "fxml/stream/PresentationWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(presentationWindowController, stage);
    windowControllers.put("PresentationWindowController", presentationWindowController);
  }

  /**
   * Creates a MediaPlayerController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showMediaPlayerWindow(Stage stage) {
    BaseController mediaPlayerController =
        new MediaPlayerController(this, "fxml/stream/MediaPlayerWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(mediaPlayerController, stage);
    windowControllers.put("MediaPlayerController", mediaPlayerController);
  }

  /**
   * Creates a WebcamWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Stage to the ViewInitialiser for setup.
   *
   * @param stage
   *        The Stage to contain the new Scene
   */
  public void showWebcamWindow(Stage stage) {
    BaseController webcamWindowController =
        new WebcamWindowController(this, "fxml/stream/WebcamWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(webcamWindowController, stage);
    windowControllers.put("WebcamWindowController", webcamWindowController);
  }

  /**
   * .
   */
  public void showTextChatWindow(Stage stage) {
    BaseController textChatWindowController =
        new TextChatWindowController(this, "fxml/TextChatWindow.fxml", mainConnection, 89, 4);
    viewInitialiser.initialiseStage(textChatWindowController, stage);
    windowControllers.put("TextChatWindowController", textChatWindowController);
  }

  /**
   * Creates a ProfileWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @param  mainWindowController
   *         ######################
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedProfileWindow(AnchorPane anchorPane,
      MainWindowController mainWindowController) throws IOException {
    BaseController profileWindowController = new ProfileWindowController(this,
        "fxml/main/ProfileWindow.fxml", mainConnection, mainWindowController);
    viewInitialiser.initialiseEmbeddedStage(profileWindowController, anchorPane);
    windowControllers.put("ProfileWindowController", profileWindowController);
  }

  /**
   * Creates a StreamWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @param  account
   *         The user Account created once past the login stage
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedStreamWindow(AnchorPane anchorPane, Account account, int sessionID, boolean isHost) throws IOException {
    BaseController streamWindowController = new StreamWindowController(this,
        "fxml/main/StreamWindow.fxml", mainConnection, account, sessionID, isHost);
    viewInitialiser.initialiseEmbeddedStage(streamWindowController, anchorPane);
    windowControllers.put("StreamWindowController", streamWindowController);
  }

  /**
   * Creates a DiscoverWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @param  mainWindowController
   *         ######################
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedDiscoverWindow(AnchorPane anchorPane,
      MainWindowController mainWindowController) throws IOException {
    BaseController discoverWindowController = new DiscoverWindowController(this,
        "fxml/main/DiscoverWindow.fxml", mainConnection, mainWindowController);
    viewInitialiser.initialiseEmbeddedStage(discoverWindowController, anchorPane);
    windowControllers.put("DiscoverWindowController", discoverWindowController);
  }

  /**
   * Creates a SubjectWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedSubjectWindow(AnchorPane anchorPane, MainWindowController mainWindowController,
      int subject) throws IOException {
    BaseController subjectWindowController
        = new SubjectWindowContoller(this, "fxml/discover/SubjectWindow.fxml",
            mainConnection, mainWindowController, subject, anchorPane);
    viewInitialiser.initialiseEmbeddedStage(subjectWindowController, anchorPane);
    windowControllers.put("SubjectWindowContoller", subjectWindowController);
  }

  /**
   * Creates a MediaPlayerController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedMediaPlayerWindow(AnchorPane anchorPane) throws IOException {
    BaseController mediaPlayerController = new MediaPlayerController(this,
        "fxml/stream/MediaPlayerWindow.fxml", mainConnection);
    viewInitialiser.initialiseEmbeddedStage(mediaPlayerController, anchorPane);
    windowControllers.put("MediaPlayerController", mediaPlayerController);
  }

  /**
   * Creates a WhiteboardWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedWhiteboardWindow(AnchorPane anchorPane, int userID, int sessionID) throws IOException {
    // TODO Set the userID and sessionID to integers that are passed in.
    BaseController whiteboardWindowController = new WhiteboardWindowController(this,
        "fxml/stream/WhiteboardWindow.fxml", mainConnection, "userId-000", "session-000");
    viewInitialiser.initialiseEmbeddedStage(whiteboardWindowController, anchorPane);
    windowControllers.put("WhiteboardWindowController", whiteboardWindowController);
  }

  /**
   * Creates a PresentationWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedPresentationWindow(AnchorPane anchorPane) throws IOException {
    BaseController presentationWindowController = new PresentationWindowController(this,
        "fxml/stream/PresentationWindow.fxml", mainConnection);
    viewInitialiser.initialiseEmbeddedStage(presentationWindowController, anchorPane);
    windowControllers.put("PresentationWindowController", presentationWindowController);
  }

  /**
   * Creates a TextChatWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the View Initialiser for setup.
   *
   * @param  anchorPane
   *         The Anchor Pane to contain the new Scene
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedTextChatWindow(AnchorPane anchorPane, int userID, int sessionID) throws IOException {
    BaseController textChatWindowController = new TextChatWindowController(this,
        "fxml/Stream/TextChatWindow.fxml", mainConnection, userID, sessionID);
    viewInitialiser.initialiseEmbeddedStage(textChatWindowController, anchorPane);
    windowControllers.put("TextChatWindowController", textChatWindowController);
  }

  /**
   * Creates a HomeWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param anchorPane
   *        The Anchor Pane to contain the new Scene
   *
   * @param  mainWindowController
   *         ######################
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedHomeWindow(AnchorPane anchorPane,
      MainWindowController mainWindowController) throws IOException {
    BaseController homeWindowController = new HomeWindowController(this,
        "fxml/main/HomeWindow.fxml", mainConnection, mainWindowController);
    viewInitialiser.initialiseEmbeddedStage(homeWindowController, anchorPane);
    windowControllers.put("HomeWindowController", homeWindowController);
  }

  /**
   * Creates a SubscriptionsWindowController, connect it to the
   * associated FXML file and sends this along with the
   * supplied Anchor Pane to the ViewInitialiser for setup.
   *
   * @param anchorPane
   *        The Anchor Pane to contain the new Scene
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found
   */
  public void embedSubscriptionsWindow(AnchorPane anchorPane) throws IOException {
    BaseController subscriptionsWindowController = new SubscriptionsWindowController(this,
        "fxml/main/SubscriptionsWindow.fxml", mainConnection);
    viewInitialiser.initialiseEmbeddedStage(subscriptionsWindowController, anchorPane);
    windowControllers.put("SubscriptionsWindowController", subscriptionsWindowController);
  }

  public HashMap<String, BaseController> getWindowControllers() {
    return windowControllers;
  }
}
