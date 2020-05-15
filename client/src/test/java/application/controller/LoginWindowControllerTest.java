package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.mockito.Mock;

/**
 * CLASS DESCRIPTION:
 * This class tests the LoginWindowController. It tests
 * the functionality of pressing the login button with and
 * without text in the username and password fields.
 *
 * @author James Gardner
 * @see LoginWindowController
 */
public class LoginWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  /* Creating local JavaFX Objects for testing. */
  protected TextField usernameField;
  protected PasswordField passwordField;
  protected Label errorLabel;
  protected LoginWindowController loginWindowController;
  protected ImageView loaderIcon;
  protected LoginService loginService;
  volatile boolean threadDone;

  /**
   * This is testing pressing the Login Button before entering a
   * String into the username and password fields.
   */
  public void testFieldsValidation() {
    loginWindowController.loginButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Username");
    usernameField.setText("someUsername");
    loginWindowController.loginButtonAction();
    assertEquals(errorLabel.getText(), "Please Enter Password");
    System.out.println("Tested Login Fields Validation");
  }

  /** 
   * This is testing that the loginService is started correctly once
   * Strings are in both fields and the user presses the Login Button.
   */
  public void testLoginAction() {
    threadDone = false;
    Platform.runLater(() -> {
      usernameField.setText("someUsername");
      passwordField.setText("password");
      loginWindowController.loginButtonAction();
      System.out.println("Tested Login Action");
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}
