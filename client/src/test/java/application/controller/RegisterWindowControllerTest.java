package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mockito.Mock;

public class RegisterWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected RegisterService registerServiceMock;

  /* Creating local JavaFX Objects for testing. */
  protected TextField usernameField;

  protected TextField emailField;

  protected TextField emailConfirmField;

  protected PasswordField passwordField;

  protected PasswordField passwordConfirmField;

  protected Label errorLabel;

  protected CheckBox isTutorCheckBox;

  protected RegisterWindowController registerWindowController;

  /**
   * This is testing pressing the Login Button before entering a
   * String into the username and password fields.
   */
  public void testFieldsValidation() {
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Username");
    usernameField.setText("someUsernameThatIsTooLong");
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Username Too Long");
    usernameField.setText("someUsername");
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Email");
    emailField.setText("someEmail");
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Emails Don't Match");
    emailConfirmField.setText("someEmail");
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Password");
    passwordField.setText("somePassword");
    registerWindowController.signUpButtonAction();
    assertEquals(errorLabel.getText(), "Passwords Don't Match");
    passwordConfirmField.setText("somePassword");
    registerWindowController.signUpButtonAction();
    System.out.println("Tested Register Fields Action");
  }

  /**
   * This is testing that the loginService is started correctly once
   * Strings are in both fields and the user presses the Login Button.
   */
  public void testRegisterAction() {
    Platform.runLater(() -> {
      usernameField.setText("someUsername");
      emailField.setText("someEmail");
      emailConfirmField.setText("someEmail");
      passwordField.setText("password");
      passwordConfirmField.setText("password");
      registerWindowController.signUpButtonAction();
      verify(registerServiceMock).setAccount(any());
      verify(registerServiceMock).start();
      System.out.println("Tested Register Action");
    });
  }

  /**
   * METHOD DESCRIPTION.
   */
  public void testBackButtonAction() {
    Platform.runLater(() -> {
      registerWindowController.backButtonAction();
      verify(viewFactoryMock).showLoginWindow();
    });
  }
}
