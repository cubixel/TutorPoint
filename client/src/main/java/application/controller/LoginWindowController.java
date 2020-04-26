package application.controller;

import application.controller.enums.AccountLoginResult;
import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the controller for the Login Window. It contains the
 * logic for the LoginWindow.fxml Scene. All controllers must
 * extend the {@code BaseController}.
 *
 * <p>The {@code LoginWindowController} initialises the Window with a set
 * of images and sets the css styles associated with the Buttons
 * used.
 *
 * <p>The {@code LoginWindowController} has a {@code LoginService} that is
 * used to send the login request to the Server with the details provided
 * by the user. Only a minor amount of checking is done on the Username
 * and Password fields to check they are not blank. This is due to the
 * checks being properly done on the Server side during the login process.
 *
 * <p>The password provided is immediately hashed for security.
 *
 * @author James Gardner
 * @author Stijn Marynissen
 * @see    BaseController
 * @see    LoginService
 * @see    Security
 */
public class LoginWindowController extends BaseController implements Initializable {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private Button signUpButton;

  @FXML
  private Label errorLabel;

  @FXML
  private CheckBox rememberMeCheckBox;

  @FXML
  private ImageView loaderIcon;

  private LoginService loginService;

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("LoginWindowController");


  /**
   * This is the default constructor. LoginWindowController
   * extends the BaseController class. The constructor then
   * instantiates a new LoginService.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   */
  public LoginWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.loginService = new LoginService(null, mainConnection);
  }

  /**
   * This constructor is used for testing the LoginWindowController
   * Class. It enables access to fields so input can be simulated
   * or allows Mockito Mocks to be used in place of some objects.
   *
   * @param viewFactory
   *        The viewFactory used for changing scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param usernameField
   *        A JavaFX TextField used to simulate user input
   *
   * @param passwordField
   *        A JavaFX PasswordField used to simulate user input
   *
   * @param errorLabel
   *        A JavaFX Label to display error messages
   *
   * @param loginService
   *        A JavaFX Service used to communicate with the Server and login the user
   */
  public LoginWindowController(ViewFactory viewFactory, String fxmlName,
        MainConnection mainConnection, TextField usernameField, PasswordField passwordField,
        Label errorLabel, LoginService loginService) {
    super(viewFactory, fxmlName, mainConnection);
    this.usernameField = usernameField;
    this.passwordField = passwordField;
    this.errorLabel = errorLabel;
    this.loginService = loginService;
  }

  /**
   * Action associated with pressing the loginButton. This is set in
   * the LoginWindow.fxml file. If both a username and password are
   * provided then the {@code LoginService} is started.
   */
  @FXML
  void loginButtonAction() {
    if (fieldsAreValid()) {
      /* Creates a new Account object with the username provided and a
      * hashed version of the provided password. */
      Account account = new Account(usernameField.getText(),
          Security.hashPassword(passwordField.getText()));
      loginService.setAccount(account);
      loaderIcon.setVisible(true);
      if (!loginService.isRunning()) {
        loginService.reset();
        loginService.start();
      } else {
        log.warn("LoginService is still running");
        /* This can occur if the user has already pressed the login button
        * and there is an issue or delay with the Client-Server connection. */
      }

      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();
        loaderIcon.setVisible(false);

        switch (result) {
          case LOGIN_SUCCESS:
            log.info("LoginWindowController: Login, Successful");
            if (rememberMeCheckBox.isSelected()) {
              try {
                // TODO Very basic just to get some functionality working.
                // Could implement something like this:
                // https://stackoverflow.com/questions/1354999/keep-me-logged-in-the-best-approach
                FileWriter writer =
                    new FileWriter("client/src/main/resources/application/model/userLoggedIn.txt");
                writer.write(account.getUsername() + "\n");
                writer.write(passwordField.getText());
                writer.close();
              } catch (IOException e) {
                log.error("Could not save login details", e);
              }
            }

            /* Need access to the current Stage, this is provided to the
             * ViewFactory to swap the Scene with the MainWindow.fxml. This is
             * the only way to get access to the Stage. */
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            viewFactory.showMainWindow(stage, account);
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Wrong Username or Password");
            log.warn("FAILED_BY_CREDENTIALS");
            break;
          case FAILED_BY_UNEXPECTED_ERROR:
            errorLabel.setText("Unexpected Error");
            log.error("FAILED_BY_UNEXPECTED_ERROR");
            break;
          case FAILED_BY_NETWORK:
            errorLabel.setText("Network Error");
            break;
          default:
            errorLabel.setText("Unknown Error");
        }
      });
    }
  }

  /**
   * Changes the Scene to the RegisterWindow.fxml if the
   * user presses the SignUp Button.
   */
  @FXML
  void signUpButtonAction() {
    Stage stage = (Stage) errorLabel.getScene().getWindow();
    viewFactory.showRegisterWindow(stage);
  }

  /**
   * This checks that the {@link #usernameField} and {@link #passwordField} have
   * has some text entered. It does not check if these follow the rules of TutorPoints
   * login process as this will be done on the Server side when they don't match an
   * account.
   *
   * @return {@code false} if {@link #usernameField} or {@link #passwordField}
   *         are empty, otherwise {@code true}.
   */
  private boolean fieldsAreValid() {
    if (usernameField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Username");
      log.info("UsernameField is empty");
      return false;
    }
    if (passwordField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Password");
      log.info("PasswordField is empty");
      return false;
    }
    return true;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loaderIcon.setVisible(false);
  }
}
