package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.view.ViewFactory;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the RegisterWindowController. It tests
 * the functionality of checking that user details are
 * valid and that the correct methods are called when a
 * user presses register.
 *
 * @author James Gardner
 * @see RegisterWindowController
 */
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

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("RegisterWindowControllerTest");

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
    assertEquals(errorLabel.getText(), "Use 8 or more characters with mix-case letters,"
        + "\nnumbers & symbols");
    log.info("Tested Register Fields Action");
  }
}
