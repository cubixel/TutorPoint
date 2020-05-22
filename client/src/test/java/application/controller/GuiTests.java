package application.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountUpdateResult;
import application.controller.presentation.PresentationSlideTest;
import application.controller.services.LoginService;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
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
      loaderIcon = new ImageView();
      loginService = new LoginService(null, mainConnectionMock);

      loginWindowController = new LoginWindowController(viewFactoryMock, null,
          mainConnectionMock, usernameField, passwordField, errorLabel, loginService,
          loaderIcon);

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

      whiteboard = new Whiteboard(new Canvas(), new Canvas());
      widthSlider = new Slider();
      colorPicker = new ColorPicker();
      accessCheckBox = new CheckBox();
      penButton = new ToggleButton();
      highlighterButton = new ToggleButton();
      eraserButton = new ToggleButton();
      squareButton = new ToggleButton();
      circleButton = new ToggleButton();
      lineButton = new ToggleButton();
      textButton = new ToggleButton();

      whiteboardWindowController = new WhiteboardWindowController(viewFactoryMock,
          null, mainConnectionMock, whiteboard, whiteboardServiceMock,
          colorPicker, widthSlider, accessCheckBox, penButton, highlighterButton, eraserButton,
          squareButton, circleButton, lineButton, textButton);
    }

    @Test
    public void doTestWhiteboardInitialisation() {
      testWhiteboardInitialisation();
    }

    @Test
    public void doTestCanvasToolSelect() {
      testCanvasToolSelect();
    }

    @Test
    public void doTestColorPicker() {
      testColorPicker();
    }

    @Test
    public void doTestWidthSlider() {
      testWidthSlider();
    }

    @Test
    public void doTestDrawLine() {
      testDrawLine();
    }
  }

  @Nested
  class TextChatTest extends TextChatWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      // init GUI
      textChatInput = new TextField();
      textChatSendButton = new Button();

      textChatWindowController = new TextChatWindowController(viewFactoryMock,
          null, mainConnectionMock, 0, 0,
          textChatInput, textChatSendButton);
    }

    @Test
    public void doTestTextChatInitialise() {
      testTextChatInitialise();
    }

    @Test
    public void doTextChatTestSendButton() {
      testSendButton();
    }

    @Test
    public void doTextChatTestEnterKey() {
      testEnterKey();
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

  @Nested
  class SlideTest extends PresentationSlideTest {
    @Test
    public void doMakeSlide() {
      makeSlide();
    }

    @Test
    public void doTestXmlNoId() {
      testXmlNoId();
    }

    @Test
    public void doTestXmlBadId() {
      testXmlBadId();
    }

    @Test
    public void doTestXmlNoDuration() {
      testXmlNoDuration();
    }

    @Test
    public void doTestXmlBadDuration() {
      testXmlBadDuration();
    }

    @Test
    public void doTestXmlNoAttributes() {
      testXmlNoAttributes();
    }

    @Test
    public void doAddValidText() {
      addValidText();
    }
    
    @Test
    public void doIgnoreInvalidText() {
      ignoreInvalidText();
    }

    @Test
    public void doAddValidLine() {
      addValidLine();
    }

    @Test
    public void doIgnoreInvalidLine() {
      ignoreInvalidLine();
    }

    @Test
    public void doAddValidShape() {
      addValidShape();
    }

    @Test
    public void doIgnoreInvalidShape() {
      ignoreInvalidShape();
    }

    @Test
    public void doAddValidShading() {
      addValidShading();
    }

    @Test
    public void doIgnoreInvalidShading() {
      ignoreInvalidShading();
    }

    @Test
    public void doAddValidAudio() {
      addValidAudio();
    }

    @Test
    public void doIgnoreInvalidAudio() {
      ignoreInvalidAudio();
    }

    @Test
    public void doAddValidImage() {
      addValidImage();
    }

    @Test
    public void doIgnoreInvalidImage() {
      ignoreInvalidImage();
    }

    @Test
    public void doAddValidVideo() {
      addValidVideo();
    }

    @Test
    public void doIgnoreInvalidVideo() {
      ignoreInvalidVideo();
    }

    @Test
    public void doTestMixedSlide() {
      testMixedSlide();
    }
  }

  /*@Nested
  class ExtendoImageHandlerTest extends ImageHandlerTest {

  }*/
}
