package application.controller;

import application.controller.enums.AccountRegisterResult;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
  private Label errorLabel;

  @FXML
  private TextField usernameField;

  @FXML
  private CheckBox isTutorCheckBox;

  @FXML
  private Button registerButton;

  @FXML
  private Button backButton;

  @FXML
  private TextField emailField;

  @FXML
  private TextField emailConfirmField;

  @FXML
  private PasswordField passwordConfirmField;

  @FXML
  private PasswordField passwordField;

  private RegisterService registerService;


  @FXML
  void registerButtonAction() {
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
          case ACCOUNT_REGISTER_SUCCESS:
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

    if (!Security.usernameIsValid(usernameField.getText(), errorLabel)) {
      return false;
    }

    if (!Security.emailIsValid(emailField.getText(), emailConfirmField.getText(), errorLabel)) {
      return false;
    }

    return Security.passwordIsValid(passwordField.getText(), passwordConfirmField.getText(),
        errorLabel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    usernameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
    emailField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
    emailConfirmField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
    passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
    passwordConfirmField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
    isTutorCheckBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          registerButtonAction();
        }
      }
    });
  }
}
