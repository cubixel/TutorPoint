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
import javafx.scene.control.TextField;

public class ChangeEmailPopUpController extends BaseController {

  private Account account;
  private UpdateDetailsService updateDetailsService;

  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private TextField newEmailField;

  @FXML
  private TextField confirmNewEmailField;

  @FXML
  private Label errorLabel;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *  @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public ChangeEmailPopUpController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
    updateDetailsService = new UpdateDetailsService(null, mainConnection);
  }

  @FXML
  void updateButtonAction() {
    if (fieldsAreValid()) {
      account.setHashedpw(Security.hashPassword(confirmPasswordField.getText()));
      AccountUpdate accountUpdate = new AccountUpdate(account, "null",
          newEmailField.getText(), "null", -1);
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
            System.out.println("Updated Email!");
            errorLabel.setText("Success");
            account.setEmailAddress(newEmailField.getText());
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Incorrect Password");
            break;
          case FAILED_BY_EMAIL_TAKEN:
            errorLabel.setText("Email Already Registered");
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

    if (!Security.emailIsValid(newEmailField.getText(), confirmNewEmailField.getText(),
        errorLabel)) {
      return false;
    }

    if (confirmPasswordField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }
    return true;
  }

}
