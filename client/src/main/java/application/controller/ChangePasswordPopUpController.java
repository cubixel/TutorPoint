package application.controller;

import application.controller.enums.AccountUpdateResult;
import application.controller.services.MainConnection;
import application.controller.services.UpdateDetailsService;
import application.controller.tools.Security;
import application.model.Account;
import application.model.AccountUpdate;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePasswordPopUpController extends BaseController {

  private Account account;
  private UpdateDetailsService updateDetailsService;

  @FXML
  private PasswordField passwordField;

  @FXML
  private PasswordField passwordConfirmField;

  @FXML
  private PasswordField currentPasswordField;

  @FXML
  private Label errorLabel;


  /**
   * CONSTRUCTOR DESCRIPTION.
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public ChangePasswordPopUpController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    updateDetailsService = new UpdateDetailsService(null, mainConnection);
  }

  @FXML
  void updateButtonAction() {
    if (fieldsAreValid()) {
      account.setHashedpw(Security.hashPassword(currentPasswordField.getText()));
      AccountUpdate accountUpdate = new AccountUpdate(account, "null",
          "null", Security.hashPassword(passwordField.getText()), -1);
      updateDetailsService.setAccountUpdate(accountUpdate);

      if (!updateDetailsService.isRunning()) {
        updateDetailsService.reset();
        updateDetailsService.start();
      } else {
        System.out.println("Error as UpdateDetailsService is still running.");
      }

      updateDetailsService.setOnSucceeded(event -> {
        AccountUpdateResult result = updateDetailsService.getValue();

        switch (result) {
          case SUCCESS:
            System.out.println("Updated Password!");
            errorLabel.setText("Success");
            account.setHashedpw(Security.hashPassword(passwordField.getText()));
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Incorrect Password");
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

  private boolean fieldsAreValid() {

    if (!Security.passwordIsValid(passwordField.getText(), passwordConfirmField.getText(), errorLabel)) {
      return false;
    }

    if (currentPasswordField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Current Password");
      return false;
    }
    return true;
  }

}
