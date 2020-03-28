/*
 * ViewFactory.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.view;

import application.controller.BaseController;
import application.controller.ChangeEmailPopUpController;
import application.controller.ChangePasswordPopUpController;
import application.controller.ChangeTutorStatusPopUpController;
import application.controller.ChangeUsernamePopUpController;
import application.controller.LoginWindowController;
import application.controller.MainWindowController;
import application.controller.MediaPlayerController;
import application.controller.PresentationWindowController;
import application.controller.ProfileWindowController;
import application.controller.RegisterWindowController;
import application.controller.StreamWindowController;
import application.controller.WebcamWindowController;
import application.controller.WhiteboardWindowController;
import application.controller.services.MainConnection;
import application.model.Account;
import java.io.IOException;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * CLASS DESCRIPTION:
 * This class is used to generate and the views that
 * the user will see. It contains the methods used to generate
 * a 'Controller' and then a 'Stage' for particular .fxml
 * windows.
 *
 * @author CUBIXEL
 *
 */
public class ViewFactory {

  /* This is the main connection to the server. */
  private MainConnection mainConnection;
  private ViewInitialiser viewInitialiser;

  /**
   * Constructor for the ViewFactory. Needs access
   * to the main client-server connection in order
   * to distribute this to the controllers.
   *
   * @param mainConnection A connection to a Server.
   */
  public ViewFactory(MainConnection mainConnection) {
    this(mainConnection, new ViewInitialiser());
  }

  /**
   * DESCRIPTION.
   */
  public ViewFactory(MainConnection mainConnection, ViewInitialiser viewInitialiser) {
    this.mainConnection = mainConnection;
    this.viewInitialiser = viewInitialiser;
  }

  /**
   * DESCRIPTION.
   */
  public void showLoginWindow(Stage stage) {
    /* Each window needs a controller specific to it. This
     * is creating a new LoginWindowController using the
     * Abstract class BaseController. The LoginWindow.fxml
     * is passed in as an argument. */
    BaseController loginWindowController =
        new LoginWindowController(this, "fxml/LoginWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(loginWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showMainWindow(Stage stage, Account account) {
    /* The MainWindowController takes the MainWindow.fxml
     * as its argument. These fxml files must be placed in
     * the correct folder: resources -> view -> fxml */
    BaseController mainWindowController =
        new MainWindowController(this, "fxml/MainWindow.fxml", mainConnection, account);
    viewInitialiser.initialiseStage(mainWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showRegisterWindow(Stage stage) {
    BaseController registerWindowController =
        new RegisterWindowController(this, "fxml/RegisterWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(registerWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showRegisterWindowNew(Stage stage) {
    BaseController registerWindowController =
        new RegisterWindowController(this, "fxml/RegisterWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(registerWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showWhiteboardWindow(Stage stage) {
    BaseController whiteboardWindowController =
        new WhiteboardWindowController(this, "fxml/WhiteboardWindow.fxml", mainConnection, "userID-000");
    viewInitialiser.initialiseStage(whiteboardWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showPresentationWindow(Stage stage) {
    BaseController controller =
        new PresentationWindowController(this, "fxml/PresentationWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(controller, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showMediaPlayerWindow(Stage stage) {
    BaseController mediaPlayerController =
        new MediaPlayerController(this, "fxml/MediaPlayerWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(mediaPlayerController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showWebcamWindow(Stage stage) {
    BaseController webcamWindowController =
        new WebcamWindowController(this, "fxml/WebcamWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(webcamWindowController, stage);
  }

  /**
   * DESCRIPTION.
   */
  public void showStreamWindow(Stage stage) {
    BaseController controller =
        new StreamWindowController(this, "fxml/StreamWindow.fxml", mainConnection);
    viewInitialiser.initialiseStage(controller, stage);
  }

  public void embedProfileWindow(AnchorPane anchorPane, Account account) throws IOException {
    BaseController profileWindowController = new ProfileWindowController(this,
        "fxml/ProfileWindow.fxml", mainConnection, account);
    viewInitialiser.initialiseEmbeddedStage(profileWindowController, anchorPane);
  }

  public void embedPasswordPopUp(AnchorPane anchorPane, Account account) throws IOException {
    BaseController changePasswordPopUpController = new ChangePasswordPopUpController(this,
        "fxml/popups/ChangePasswordPopUp.fxml", mainConnection, account);
    viewInitialiser.initialiseEmbeddedStage(changePasswordPopUpController, anchorPane);
  }

  public void embedUsernamePopUp(AnchorPane anchorPane, Account account) throws IOException {
    BaseController changeUsernamePopUpController = new ChangeUsernamePopUpController(this,
        "fxml/popups/ChangeUsernamePopUp.fxml", mainConnection, account);
    viewInitialiser.initialiseEmbeddedStage(changeUsernamePopUpController, anchorPane);
  }

  public void embedEmailPopUp(AnchorPane anchorPane, Account account) throws IOException {
    BaseController changeEmailPopUpController = new ChangeEmailPopUpController(this,
        "fxml/popups/ChangeEmailPopUp.fxml", mainConnection, account);
    viewInitialiser.initialiseEmbeddedStage(changeEmailPopUpController, anchorPane);
  }

  public void embedTutorStatusPopUp(AnchorPane anchorPane, Account account) throws IOException {
    BaseController changeTutorStatusPopUpController = new ChangeTutorStatusPopUpController(this,
        "fxml/popups/ChangeTutorStatusPopUp.fxml", mainConnection, account);
    viewInitialiser.initialiseEmbeddedStage(changeTutorStatusPopUpController, anchorPane);
  }
}
