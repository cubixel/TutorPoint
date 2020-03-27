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
import javafx.scene.layout.AnchorPane;

public class ProfileWindowController extends BaseController implements Initializable {

  private Account account;

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Label profileNameLabel;

  @FXML
  private Label emailAddressLabel;

  @FXML
  private Label tutorStatusLabel;

  @FXML
  private AnchorPane anchorPanePassword;

  @FXML
  private AnchorPane anchorPaneUsername;

  @FXML
  private Button updatePasswordButton;

  @FXML
  private Button updateEmailButton;

  @FXML
  private AnchorPane anchorPaneEmail;

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
    updatePasswordButton.toBack();
    updateEmailButton.toBack();
    try {
      viewFactory.embedUsernamePopUp(anchorPaneUsername, account);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void updatePasswordAction() {
    updateEmailButton.toBack();
    try {
      viewFactory.embedPasswordPopUp(anchorPanePassword, account);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @FXML
  void updateEmailAction() {
    try {
      viewFactory.embedEmailPopUp(anchorPaneEmail, account);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @FXML
  void anchorPaneClickedAction() {
    anchorPaneUsername.getChildren().clear();
    anchorPanePassword.getChildren().clear();
    anchorPaneEmail.getChildren().clear();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (account != null) {
      profileNameLabel.setText(account.getUsername());
      emailAddressLabel.setText(account.getEmailAddress());

      if (account.getTutorStatus() == 0) {
        tutorStatusLabel.setText("Student Account");
      } else {
        tutorStatusLabel.setText("Tutor Account");
      }

    }
  }
}
