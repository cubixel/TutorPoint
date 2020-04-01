package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.MainConnection;
import application.controller.services.UpdateDetailsService;
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
    Platform.setImplicitExit(false);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        String newEmail = "newemail@cubixel.com";
        assertEquals(email, account.getEmailAddress());
        newEmailField.setText(newEmail);
        confirmNewEmailField.setText(newEmail);
        currentPasswordForEmailField.setText(password);
        profileWindowController.updateEmailAction();
        System.out.println("here");
        assertEquals("Success", emailErrorLabel.getText());
        assertEquals(newEmail, account.getEmailAddress());
      }
    });
  }

  /**
   * TODO.
   */
  public void updatePasswordActionTest() {
    Platform.runLater(() -> {
    });
  }


  /**
   * TODO.
   */
  public void updateTutorStatusActionTest() {
    Platform.runLater(() -> {
    });
  }

  /**
   * TODO.
   */
  public void updateUsernameActionTest() {
    Platform.runLater(() -> {
    });
  }

  /**
   * Todo.
   */
  public void updateAccountViewsTest() {
    assertEquals(username, usernameLabel.getText());
    assertEquals(email, emailAddressLabel.getText());
    assertEquals("Tutor Account", tutorStatusLabel.getText());
  }

}
