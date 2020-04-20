package application.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountUpdateResult;
import application.model.Account;
import application.model.Whiteboard;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
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
  class LoginTest extends LoginWindowControllerTest {

    @BeforeEach
    public void setUp() {
      /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
      initMocks(this);
      usernameField = new TextField();
      passwordField = new PasswordField();
      errorLabel = new Label();

      loginWindowController = new LoginWindowController(viewFactoryMock, null,
          mainConnectionMock, usernameField, passwordField, errorLabel, loginServiceMock);
    }

    @Test
    public void doTestFieldsValidation() {
      testFieldsValidation();
    }

    @Test
    public void doTestLoginAction() {
      testLoginAction();
    }

    @Test
    public void doTestSignUpButtonAction() {
      testSignUpButtonActionAction();
    }

  }

  @Nested
  class RegisterTest extends RegisterWindowControllerTest {

    @BeforeEach
    public void setUp() {
      /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
      initMocks(this);
      usernameField = new TextField();
      passwordField = new PasswordField();
      passwordConfirmField = new PasswordField();
      emailField = new TextField();
      emailConfirmField = new TextField();
      errorLabel = new Label();
      isTutorCheckBox = new CheckBox();

      registerWindowController = new RegisterWindowController(viewFactoryMock, null,
          mainConnectionMock, usernameField, emailField, emailConfirmField, passwordField,
          passwordConfirmField, errorLabel, isTutorCheckBox, registerServiceMock);
    }

    @Test
    public void doTestFieldsValidation() {
      testFieldsValidation();
    }

    @Test
    public void doTestRegisterAction() {
      testRegisterAction();
    }

    @Test
    public void doTestBackButtonAction() {
      testBackButtonAction();
    }

  }

  @Nested
  class WhiteboardTest extends WhiteboardWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      whiteboard = new Whiteboard(new Canvas(), new Canvas(), "test-ID");
      widthSlider = new Slider();
      colorPicker = new ColorPicker();

      whiteboardWindowController = new WhiteboardWindowController(viewFactoryMock,
          null, mainConnectionMock, whiteboard, whiteboardServiceMock,
          colorPicker, widthSlider);
    }

    @Test
    public void doTestWhiteboardInitialisation() {
      testWhiteboardInitialisation();
    }

    @Test
    public void doTestSelectTool() {
      testSelectTool();
    }

    @Test
    public void doTestChangeColor() {
      testChangeColor();
    }

    @Test
    public void doTestChangeWidth() {
      testChangeWidth();
    }

    @Test
    public void doTestDrawLine() {
      testDrawLine();
    }
  }

  @Nested
  class ProfileWindowTest extends ProfileWindowControllerTest {

    @BeforeEach
    public void setUp() throws IOException {
      /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
      initMocks(this);

      try {
        when(mainConnectionMock.listenForString()).thenReturn(
            String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS));
      } catch (IOException e) {
        fail(e);
      }

      username = "Cubixel";
      email = "test@cubixel.com";
      password = "testP4ss!word!";

      account = new Account(username, email, password, 0, 0);

      updatePasswordButton = new Button();
      updateUsernameButton = new Button();
      updateEmailButton = new Button();
      updateTutorStatusButton = new Button();
      usernameErrorLabel = new Label();
      emailErrorLabel = new Label();
      passwordErrorLabel = new Label();
      tutorStatusErrorLabel = new Label();
      usernameLabel = new Label();
      emailAddressLabel = new Label();
      tutorStatusLabel = new Label();
      newUsernameField = new TextField();
      newEmailField = new TextField();
      confirmNewEmailField = new TextField();
      currentPasswordForUsernameField = new PasswordField();
      passwordField = new PasswordField();
      passwordConfirmField = new PasswordField();
      currentPasswordForPasswordField = new PasswordField();
      currentPasswordForEmailField = new PasswordField();
      currentPasswordForTutorStatusField = new PasswordField();
      isTutorCheckBox = new CheckBox();

      profileWindowController = new ProfileWindowController(viewFactoryMock, null,
          mainConnectionMock, account, updatePasswordButton, updateUsernameButton,
          updateEmailButton, updateTutorStatusButton, usernameErrorLabel, emailErrorLabel,
          passwordErrorLabel, tutorStatusErrorLabel, usernameLabel, emailAddressLabel,
          tutorStatusLabel, newUsernameField, newEmailField, confirmNewEmailField,
          currentPasswordForUsernameField, passwordField, passwordConfirmField,
          currentPasswordForPasswordField, currentPasswordForEmailField,
          currentPasswordForTutorStatusField, isTutorCheckBox);

      profileWindowController.initialize(null, null);

    }

    @Test
    public void doUpdateEmailActionTest() {
      updateEmailActionTest();
    }

    @Test
    public void doUpdatePasswordActionTest() {
      updatePasswordActionTest();
    }

    @Test
    public void doUpdateTutorStatusActionTest() {
      updateTutorStatusActionTest();
    }

    @Test
    public void doUpdateUsernameActionTest() {
      updateUsernameActionTest();
    }

    @Test
    public void doUpdateAccountViewsTest() {
      updateAccountViewsTest();
    }

  }
}
