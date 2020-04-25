package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.MainConnection;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mockito.Mock;

public class ProfileWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  protected ProfileWindowController profileWindowController;

  protected String username;

  protected String email;

  protected String password;

  protected Account account;

  /* Creating local JavaFX Objects for testing. */
  protected Button updatePasswordButton;

  protected Button updateUsernameButton;

  protected Button updateEmailButton;

  protected Button updateTutorStatusButton;

  protected Label usernameErrorLabel;

  protected Label emailErrorLabel;

  protected Label passwordErrorLabel;

  protected Label tutorStatusErrorLabel;

  protected Label usernameLabel;

  protected Label emailAddressLabel;

  protected Label tutorStatusLabel;

  protected TextField newUsernameField;

  protected TextField newEmailField;

  protected TextField confirmNewEmailField;

  protected PasswordField currentPasswordForUsernameField;

  protected PasswordField passwordField;

  protected PasswordField passwordConfirmField;

  protected PasswordField currentPasswordForPasswordField;

  protected PasswordField currentPasswordForEmailField;

  protected PasswordField currentPasswordForTutorStatusField;

  protected CheckBox isTutorCheckBox;

  /**
   * TODO.
   */
  public void updateEmailActionTest() {
    Platform.runLater(() -> {
      String newEmail = "newemail@cubixel.com";
      assertEquals(email, account.getEmailAddress());
      newEmailField.setText(newEmail);
      confirmNewEmailField.setText(newEmail);
      currentPasswordForEmailField.setText(password);
      profileWindowController.updateEmailAction();
      assertEquals("Success", emailErrorLabel.getText());
      assertEquals(newEmail, account.getEmailAddress());
    });
  }

  /**
   * TODO.
   */
  public void updatePasswordActionTest() {
    Platform.runLater(() -> {
      String newPassword = "newPassW0rd!";
      passwordField.setText(newPassword);
      passwordConfirmField.setText(newPassword);
      currentPasswordForPasswordField.setText(password);
      profileWindowController.updatePasswordAction();
      assertEquals("Success", passwordErrorLabel.getText());
      assertEquals(Security.hashPassword(newPassword), account.getHashedpw());
    });
  }

  /**
   * TODO.
   */
  public void updateTutorStatusActionTest() {
    Platform.runLater(() -> {
      // String newUsername = "NewUsername";
      assertEquals(0, account.getTutorStatus());
      currentPasswordForTutorStatusField.setText(password);
      isTutorCheckBox.fire();
      profileWindowController.updateTutorStatusAction();
      assertEquals("Success", tutorStatusErrorLabel.getText());
      assertEquals(1, account.getTutorStatus());
    });
  }

  /**
   * TODO.
   */
  public void updateUsernameActionTest() {
    Platform.runLater(() -> {
      String newUsername = "NewUsername";
      assertEquals(username, account.getUsername());
      newUsernameField.setText(newUsername);
      currentPasswordForUsernameField.setText(password);
      profileWindowController.updateUsernameAction();
      assertEquals("Success", usernameErrorLabel.getText());
      assertEquals(newUsername, account.getUsername());
    });
  }

  /**
   * Todo.
   */
  public void updateAccountViewsTest() {
    assertEquals(username, usernameLabel.getText());
    assertEquals(email, emailAddressLabel.getText());
    assertEquals("Student Account", tutorStatusLabel.getText());
  }

}
