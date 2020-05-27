package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.enums.AccountUpdateResult;
import application.controller.services.MainConnection;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mockito.Mock;

/**
 * Test for the ProfileWindowController. Ensures
 * that all button actions are calling the correct
 * methods.
 *
 * @author James Gardner
 * @see ProfileWindowController
 */
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
   * Testing updateEmailAction.
   */
  public void updateEmailActionTest() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS));
    } catch (IOException e) {
      fail(e);
    }

    String newEmail = "newemail@cubixel.com";
    Platform.runLater(() -> {
      assertEquals(email, account.getEmailAddress());
      newEmailField.setText(newEmail);
      confirmNewEmailField.setText(newEmail);
      currentPasswordForEmailField.setText(password);
      profileWindowController.updateEmailAction();
    });
    long start = System.currentTimeMillis();
    long end = start + 3000;
    //noinspection StatementWithEmptyBody
    while (!emailErrorLabel.getText().equals("Success") && System.currentTimeMillis() < end) {

    }
    assertEquals("Success", emailErrorLabel.getText());
    assertEquals(newEmail, account.getEmailAddress());
  }

  /**
   * Testing updatePasswordAction.
   */
  public void updatePasswordActionTest() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS));
    } catch (IOException e) {
      fail(e);
    }

    String newPassword = "newPassW0rd!";
    Platform.runLater(() -> {
      passwordField.setText(newPassword);
      passwordConfirmField.setText(newPassword);
      currentPasswordForPasswordField.setText(password);
      profileWindowController.updatePasswordAction();
    });
    long start = System.currentTimeMillis();
    long end = start + 3000;
    //noinspection StatementWithEmptyBody
    while (!account.getHashedpw().equals(Security.hashPassword(newPassword))
        && System.currentTimeMillis() < end) {

    }
    assertEquals("Success", passwordErrorLabel.getText());
    assertEquals(Security.hashPassword(newPassword), account.getHashedpw());
  }

  /**
   * Testing updateTutorStatusAction.
   */
  public void updateTutorStatusActionTest() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS));
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      // String newUsername = "NewUsername";
      assertEquals(0, account.getTutorStatus());
      currentPasswordForTutorStatusField.setText(password);
      isTutorCheckBox.fire();
      profileWindowController.updateTutorStatusAction();
    });
    long start = System.currentTimeMillis();
    long end = start + 3000;
    //noinspection StatementWithEmptyBody
    while (!(account.getTutorStatus() == 1) && System.currentTimeMillis() < end) {

    }
    assertEquals("Success", tutorStatusErrorLabel.getText());
    assertEquals(1, account.getTutorStatus());
  }

  /**
   * Testing updateUsernameAction.
   */
  public void updateUsernameActionTest() {
    try {
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS));
    } catch (IOException e) {
      fail(e);
    }

    String newUsername = "NewUsername";
    Platform.runLater(() -> {
      assertEquals(username, account.getUsername());
      newUsernameField.setText(newUsername);
      currentPasswordForUsernameField.setText(password);
      profileWindowController.updateUsernameAction();
    });
    long start = System.currentTimeMillis();
    long end = start + 3000;
    //noinspection StatementWithEmptyBody
    while (!account.getUsername().equals(newUsername) && System.currentTimeMillis() < end) {

    }
    assertEquals("Success", usernameErrorLabel.getText());
    assertEquals(newUsername, account.getUsername());
  }

  /**
   * Testing updateAccountViews.
   */
  public void updateAccountViewsTest() {
    assertEquals(username, usernameLabel.getText());
    assertEquals(email, emailAddressLabel.getText());
    assertEquals("Student Account", tutorStatusLabel.getText());
  }
}