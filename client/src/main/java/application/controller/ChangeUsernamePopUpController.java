package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ChangeUsernamePopUpController extends BaseController {

  private Account account;

  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private TextField newUsernameField;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *  @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public ChangeUsernamePopUpController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
  }

  @FXML
  void updateButtonAction() {
  }

}