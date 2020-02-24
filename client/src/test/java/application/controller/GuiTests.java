package application.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GuiTests {

  /**
   * METHOD DESCRIPTION.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }

  @Nested
  class Login extends LoginWindowControllerTest {

    @BeforeEach
    public void setUp() {
      /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
      initMocks(this);
      usernameField = new TextField();
      passwordField = new PasswordField();
      errorLabel = new Label();

      loginWindowController = new LoginWindowController(viewFactoryMock,null, mainConnectionMock,
          usernameField, passwordField, errorLabel, loginServiceMock);
    }

    @Test
    public void doTestFieldsValidation() {
      testFieldsValidation();
    }

    @Test
    public void doTestLoginAction() {
      testLoginAction();
    }

  }

  @Nested
  class Register extends RegisterWindowControllerTest {
      
    @BeforeEach
    public void setUp() {
      /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
      initMocks(this);
      usernameField = new TextField();
      passwordField = new PasswordField();
      errorLabel = new Label();
      isTutorCheckBox = new CheckBox();

      registerWindowController = new RegisterWindowController(viewFactoryMock, null,
          mainConnectionMock, usernameField, passwordField, errorLabel, isTutorCheckBox,
          registerServiceMock);
    }

    @Test
    public void doTestFieldsValidation() {
      testFieldsValidation();
    }

    @Test
    public void doTestRegisterAction() {
      testRegisterAction();
    }
  }
  
}