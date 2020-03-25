package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class ChangePasswordPopUpController extends BaseController {

  private Account account;

  @FXML
  private PasswordField passwordField;

  @FXML
  private PasswordField passwordConfirmField;

  @FXML
  private PasswordField currentPasswordField;


  /**
   * CONSTRUCTOR DESCRIPTION.
   *  @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public ChangePasswordPopUpController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
  }

  @FXML
  void updateButtonAction() {
  }

}
