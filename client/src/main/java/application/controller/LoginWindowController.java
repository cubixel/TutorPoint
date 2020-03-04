package application.controller;

import application.controller.services.AccountLoginResult;
import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.controller.services.Security;
import application.model.account.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginWindowController extends BaseController {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Label errorLabel;

  private LoginService loginService;


  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public LoginWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.loginService = new LoginService(null, mainConnection);
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
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

  @FXML
  void loginButtonAction() {
    /* Triggered on user clicking login, checks users details are valid
     * before hashing the provided password and sending
     * the users account details to the server for validation.
     */
    if (fieldsAreValid()) {
      Account account = new Account(usernameField.getText(),
          Security.hashPassword(passwordField.getText()));

      loginService.setAccount(account);
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        switch (result) {
          case SUCCESS:
            System.out.println("Success!");
            viewFactory.showMainWindow();

            Stage stage = (Stage) errorLabel.getScene().getWindow();
            viewFactory.closeStage(stage);
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Wong username or Password");
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
  void registerButtonAction() {
    viewFactory.showRegisterWindow();
    Stage stage = (Stage) errorLabel.getScene().getWindow();
    viewFactory.closeStage(stage);
  }


  private boolean fieldsAreValid() {
    if (usernameField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Username");
      return false;
    }
    if (passwordField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }
    return true;
  }
}
