package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileWindowController extends BaseController implements Initializable {

  private Account account;

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Label profileNameField;

  @FXML
  private AnchorPane anchorPanePassword;

  @FXML
  private AnchorPane anchorPaneUsername;

  @FXML
  private Button updatePasswordButton;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *  @param viewFactory
   * @param fxmlName
   * @param mainConnection
   * @param account
   */
  public ProfileWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.account = account;
  }

  @FXML
  void updateUsernameAction() {
    try {
      viewFactory.embedUsernamePopUp(anchorPanePassword, account);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void updatePasswordAction() {
    try {
      viewFactory.embedPasswordPopUp(anchorPanePassword, account);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @FXML
  void anchorPaneClickedAction() {
    anchorPanePassword.getChildren().clear();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (account != null) {
      profileNameField.setText(account.getUsername());
    }
  }
}
