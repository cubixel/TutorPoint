package application.controller;

import application.controller.enums.AccountRegisterResult;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterWindowController extends BaseController implements Initializable {
  public RegisterWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.registerService = new RegisterService(null, mainConnection);
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public RegisterWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, TextField usernameField, TextField emailField,
      TextField emailConfirmField, PasswordField passwordField, PasswordField passwordConfirmField,
      Label errorLabel, CheckBox isTutorCheckBox, RegisterService registerService) {
    super(viewFactory, fxmlName, mainConnection);
    this.usernameField = usernameField;
    this.emailField = emailField;
    this.emailConfirmField = emailConfirmField;
    this.passwordField = passwordField;
    this.passwordConfirmField = passwordConfirmField;
    this.errorLabel = errorLabel;
    this.isTutorCheckBox = isTutorCheckBox;
    this.registerService = registerService;
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public RegisterWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, TextField usernameField, PasswordField passwordField,
      Label errorLabel, CheckBox isTutorCheckBox, RegisterService registerService) {
    super(viewFactory, fxmlName, mainConnection);
    this.usernameField = usernameField;
    this.passwordField = passwordField;
    this.errorLabel = errorLabel;
    this.isTutorCheckBox = isTutorCheckBox;
    this.registerService = registerService;
  }

  @FXML
  private PasswordField passwordField;

  @FXML
  private Label errorLabel;

  @FXML
  private TextField usernameField;

  @FXML
  private CheckBox isTutorCheckBox;

  @FXML
  private Button signUpButton;

  @FXML
  private Button backButton;

  @FXML
  private AnchorPane sidePane;

  @FXML
  private ImageView imageView;

  @FXML
  private TextField emailField;

  @FXML
  private TextField emailConfirmField;

  @FXML
  private PasswordField passwordConfirmField;

  private RegisterService registerService;


  @FXML
  void signUpButtonAction() {
    /*
    * On register click, validates data is entered in the fields.
    * Hashes the password and sends details to the server
    * to attempt to create the users account.
    */
    if (fieldsAreValid()) {
      Account account = new Account(usernameField.getText(), emailField.getText(),
          Security.hashPassword(passwordField.getText()),
          isTutorCheckBox.isSelected() ? 1 : 0, 1);
      registerService.setAccount(account);

      if (!registerService.isRunning()) {
        registerService.reset();
        registerService.start();
      } else {
        System.out.println("Error as registerService is still running.");
      }

      registerService.setOnSucceeded(event -> {
        AccountRegisterResult result = registerService.getValue();

        switch (result) {
          case SUCCESS:
            System.out.println("Registered!");
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            viewFactory.showLoginWindow(stage);
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Wong Username or Password");
            break;
          case FAILED_BY_USERNAME_TAKEN:
            errorLabel.setText("Username Already Taken");
            break;
          case FAILED_BY_EMAIL_TAKEN:
            errorLabel.setText("Email Address Already Registered");
            break;
          case FAILED_BY_UNEXPECTED_ERROR:
            errorLabel.setText("Unexpected Error");
            break;
          case FAILED_BY_NETWORK:
            errorLabel.setText("Network Error");
            break;
          default:
        }
      });
    }
  }

  @FXML
  void backButtonAction() {
    Stage stage = (Stage) errorLabel.getScene().getWindow();
    viewFactory.showLoginWindow(stage);
  }

  private boolean fieldsAreValid() {

    if (!usernameIsValid(usernameField.getText())) {
      return false;
    }

    if (emailField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Email");
      return false;
    }

    if (!(Objects.equals(emailField.getText(), emailConfirmField.getText()))) {
      errorLabel.setText("Emails Don't Match");
      return false;
    }

    if (!emailIsValid(emailField.getText())) {
      errorLabel.setText("Email Address Not Valid");
      return false;
    }

    return passwordIsValid(passwordField.getText(), passwordConfirmField.getText());
  }

  // TODO Move to static methods

  protected Boolean usernameIsValid(String username) {
    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    Pattern whiteSpace = Pattern.compile("[\\s]");

    if (username.isEmpty()) {
      errorLabel.setText("Please Enter Username");
      return false;
    }

    if (username.length() > 20) {
      errorLabel.setText("Username Too Long");
      return false;
    }

    if (specialCharPatten.matcher(username).find() || whiteSpace.matcher(username).find()
    || digitCasePatten.matcher(username).find()) {
      errorLabel.setText("Username should only contains letters and have no spaces");
      return false;
    }
    return true;
  }

  protected Boolean emailIsValid(String email) {
    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  private boolean passwordIsValid(String password, String confirm) {

    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern upperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");

    if (password.isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }

    if (!password.equals(confirm)) {
      errorLabel.setText("Passwords Don't Match");
      return false;
    }

    if (!specialCharPatten.matcher(password).find() || !upperCasePatten.matcher(password).find()
        || !lowerCasePatten.matcher(password).find() || !digitCasePatten.matcher(password).find()
        || password.length() < 8) {
      errorLabel.setText("Password should use 8 or more characters "
          + "with a mix of letters,\nnumbers & symbols");
      return false;
    }
    return true;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    signUpButton.getStyleClass().add("blue-button");
    backButton.getStyleClass().add("grey-button");
    sidePane.getStyleClass().add("side-pane");
    //Creating an image
    Image image = null;
    try {
      image = new Image(new FileInputStream(
          "client/src/main/resources/application/media/icons/tutorpoint_logo_with_text.png"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    //Setting the image view
    imageView.setImage(image);
  }
}
