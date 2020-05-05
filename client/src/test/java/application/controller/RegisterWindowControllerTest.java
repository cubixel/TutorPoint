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
import javafx.stage.Stage;
import org.mockito.Mock;

public class RegisterWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected RegisterService registerServiceMock;

  @Mock
  private Stage stageMock;

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
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Username");
    usernameField.setText("someUsernameThatIsTooLong");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Username Too Long");
    usernameField.setText("someUsername");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Email");
    emailField.setText("someEmail");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Emails Don't Match");
    emailConfirmField.setText("someEmail");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Email Not Valid");
    emailField.setText("someemail@test.com");
    emailConfirmField.setText("someemail@test.com");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Password");
    passwordField.setText("somePassword");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Passwords Don't Match");
    passwordConfirmField.setText("somePassword");
    registerWindowController.registerButtonAction();
    assertEquals(errorLabel.getText(), "Use 8 or more characters with a mix of letters,"
                                              + "\nnumbers & symbols");
    System.out.println("Tested Register Fields Action");
  }

  /**
   * This is testing that the loginService is started correctly once
   * Strings are in both fields and the user presses the Login Button.
   */
  public void testRegisterAction() {
    Platform.runLater(() -> {
      usernameField.setText("someUsername");
      emailField.setText("someemail@cubixel.com");
      emailConfirmField.setText("someemail@cubixel.com");
      passwordField.setText("someV4l!dPassword");
      passwordConfirmField.setText("someV4l!dPassword");
      registerWindowController.registerButtonAction();
      verify(registerServiceMock).setAccount(any());
      verify(registerServiceMock).start();
      System.out.println("Tested Register Action");
    });
  }

  /**
   * Tests that when the back button is pressed the
   * showLoginWindow method is called.
   */
  public void testBackButtonAction() {
    Platform.runLater(() -> {
      registerWindowController.backButtonAction();
      verify(viewFactoryMock).showLoginWindow(stageMock);
    });
  }

}
