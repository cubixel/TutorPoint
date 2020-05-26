package application.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountUpdateResult;
import application.controller.services.LoginService;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.Whiteboard;
import application.model.managers.MessageManager;
import application.model.managers.SubjectManager;
import application.model.managers.SubscriptionsManger;
import application.model.managers.TutorManager;
import java.io.IOException;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the top-level test for the GUI that makes
 * calls to all other gui test.
 *
 * @author Daniel Bishop
 * @author Oliver Still
 * @author Oliver Clarke
 * @author James Gardner
 * @author Cameron Smith
 */
public class GuiTests {

  private static final Logger log = LoggerFactory.getLogger("GuiTests");

  /**
   * This method starts the JavaFX runtime. The specified Runnable will then be
   * called on the JavaFX Application Thread.
   */
  @BeforeAll
  public static void setUpToolkit() {
    Platform.startup(() -> log.info("Toolkit initialized ..."));
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
    public void setUp() {
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
  class MainWindowTest extends MainWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      subjectManager = new SubjectManager();
      tutorManager = new TutorManager();
      account = new Account("TestAccount", "TestPassword");
      navbar = new TabPane();
      homeWindow = new AnchorPane();
      subscriptionsWindow = new AnchorPane();
      discoverWindow = new AnchorPane();
      profileWindow = new AnchorPane();
      streamWindow = new AnchorPane();
      buttonbar = new ButtonBar();
      logOutButton = new Button();

      for (int i = 0; i < 5; i++) {
        navbar.getTabs().add(new Tab());
      }

      mainWindowController = new MainWindowController(viewFactoryMock, null,
          mainConnectionMock, subjectManager, tutorManager, account, navbar,
          homeWindow, subscriptionsWindow, discoverWindow, profileWindow, streamWindow,
          buttonbar, logOutButton);
    }

    @Test
    public void doInitialiseNavBarStudentTest() {
      initialiseNavBarStudentTest();
    }

    @Test
    public void doInitialiseNavBarTutorTest() {
      initialiseNavBarTutorTest();
    }
  }

  @Nested
  class HomeWindowTest extends HomeWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      homeContent = new VBox();
      topSubjects = new HBox();
      topTutors = new HBox();
      usernameLabel = new Label();
      tutorStatusLabel = new Label();
      profilePane = new Pane();
      liveTutorsVbox = new VBox();
      userProfilePicture = new Circle();

      liveTutorManger = new HashMap<>();
      subjectManager = new SubjectManager();
      tutorManager = new TutorManager();

      int userID = 1;
      try {
        when(mainWindowControllerMock.getSubjectManager()).thenReturn(subjectManager);
        when(mainWindowControllerMock.getTutorManager()).thenReturn(tutorManager);
        when(mainWindowControllerMock.getAccount()).thenReturn(accountMock);
        when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
        when(accountMock.getUserID()).thenReturn(userID);
        when(mainConnectionMock.claim()).thenReturn(true);
        when(mainConnectionMock.listenForString()).thenReturn("SUBJECT_REQUEST_SUCCESS",
            "TUTOR_REQUEST_SUCCESS");
      } catch (IOException e) {
        fail(e);
      }

      homeWindowController = new HomeWindowController(viewFactoryMock, null,
          mainConnectionMock, mainWindowControllerMock, homeContent, topSubjects, topTutors,
          usernameLabel, tutorStatusLabel, profilePane, liveTutorsVbox, userProfilePicture,
          liveTutorManger);

      homeWindowController.initialize(null, null);
    }

    @Test
    public void doAddSubjectLinkTest() {
      addSubjectLinkTest();
    }

    @Test
    public void doAddTutorLinkTest() {
      addTutorLinkTest();
    }

    @Test
    public void doAddLiveTutorLinkTest() {
      addLiveTutorLinkTest();
    }
  }

  @Nested
  class SubjectWindowTest extends SubjectWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      subject = new Subject(1, "TestSubject", "TestCategory", true);
      headerImageView = new ImageView();
      followingSubjectLabel = new Label();
      followSubjectButton = new Button();
      teachSubjectButton = new Button();
      backToDiscoverButton = new Button();
      subjectLabel = new Label();

      when(mainWindowControllerMock.getAccount()).thenReturn(accountMock);

      subjectWindowController = new SubjectWindowController(viewFactoryMock, null,
          mainWindowControllerMock, mainConnectionMock, subject, headerImageView,
          followingSubjectLabel, followSubjectButton, teachSubjectButton, backToDiscoverButton,
          subjectLabel);
    }

    @Test
    public void doInitialiseAsStudentTest() {
      initialiseAsStudentTest();
    }

    @Test
    public void doInitialiseAsTutorTest() {
      initialiseAsTutorTest();
    }

    @Test
    public void doFollowingSubjectLabelAndButtonTest() {
      followingSubjectLabelAndButtonTest();
    }

    @Test
    public void doNotFollowingSubjectLabelAndButtonTest() {
      notFollowingSubjectLabelAndButtonTest();
    }

    @Test
    public void doFollowSubjectActionTest() {
      followSubjectActionTest();
    }
  }

  @Nested
  class TutorWindowTest extends TutorWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      tutor = new Tutor("TestUsername", 1, (float) 3.5, true);
      parentAnchorPane = new AnchorPane();
      backToDiscoverButton = new Button();
      tutorNameLabel = new Label();
      backToHomeButton = new Button();
      followTutorButton = new Button();
      profilePictureHolder = new Circle();
      tutorRatingLabel = new Label();
      followingTutorLabel = new Label();
      ratingSlider = new Slider();
      submitRatingButton = new Button();
      subjectsHBox = new HBox();

      when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);

      tutorWindowController = new TutorWindowController(viewFactoryMock, null,
          mainConnectionMock, mainWindowControllerMock, tutor, parentAnchorPane,
          backToDiscoverButton, tutorNameLabel, backToHomeButton, followTutorButton,
          profilePictureHolder, tutorRatingLabel, followingTutorLabel, ratingSlider,
          submitRatingButton,subjectsHBox);
    }

    @Test
    public void doFollowingTutorLabelAndButtonTest() {
      followingTutorLabelAndButtonTest();
    }

    @Test
    public void doNotFollowingTutorLabelAndButtonTest() {
      notFollowingTutorLabelAndButtonTest();
    }

    @Test
    public void doFollowTutorActionTest() {
      followTutorActionTest();
    }
  }

  @Nested
  class SubscriptionsWindowTest extends SubscriptionsWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      mainScrollBar = new ScrollBar();
      mainScrollPane = new ScrollPane();
      mainScrollContent = new AnchorPane();
      userSubject1Label = new Label();
      infoLabelOne = new Label();
      infoLabelTwo = new Label();
      userSubject1Content = new HBox();
      userSubject2Label = new Label();
      userSubject2Content = new HBox();
      subscriptionsMangerOne = new SubscriptionsManger();
      subscriptionsMangerTwo = new SubscriptionsManger();
      account = new Account("AccountTest", "PasswordTest");
      account.setUserID(1);

      try {
        when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
        when(mainConnectionMock.claim()).thenReturn(true);
        when(mainConnectionMock.listenForString()).thenReturn("SUBJECT_REQUEST_SUCCESS",
            "SUBJECT_REQUEST_SUCCESS", "SUBJECT_REQUEST_SUCCESS");
      } catch (IOException e) {
        log.error("Failed to setup Mock MainConnection");
        fail(e);
      }

      subscriptionsWindowController = new SubscriptionsWindowController(viewFactoryMock, null,
          mainConnectionMock, mainWindowControllerMock, mainScrollBar, mainScrollPane,
          mainScrollContent, userSubject1Label, infoLabelOne, infoLabelTwo, userSubject1Content,
          userSubject2Label, userSubject2Content, subscriptionsMangerOne, subscriptionsMangerTwo,
          account);

    }

    @Test
    public void doInitialiseOneFollowedSubjectTest() {
      initialiseOneFollowedSubjectTest();
    }

    @Test
    public void doInitialiseTwoFollowedSubjectsTest() {
      initialiseTwoFollowedSubjectsTest();
    }

    @Test
    public void soAddSubjectLinkTest() {
      addSubjectLinkTest();
    }
  }

  @Nested
  class StreamWindowTest extends StreamWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      account = new Account(1, "TestUser", "user@test.com",
          "password", 1, 0);
      sessionID = 1;
      isHost = true;
      isLive = false;
      primaryTabPane = new TabPane();
      anchorPaneMultiViewVideo = new AnchorPane();
      anchorPaneMultiViewPresentation = new AnchorPane();
      anchorPaneMultiViewWhiteboard = new AnchorPane();
      anchorPaneVideo = new AnchorPane();
      webcamHolderOne = new AnchorPane();
      webcamHolderTwo = new AnchorPane();
      textChatHolder = new AnchorPane();
      anchorPanePresentation = new AnchorPane();
      anchorPaneWhiteboard = new AnchorPane();
      masterPane = new AnchorPane();
      resizePane = new Pane();
      streamButton = new Button();
      disconnectButton = new Button();
      resetStream = new Button();


      when(mainConnectionMock.claim()).thenReturn(true);


      streamWindowController = new StreamWindowController(viewFactoryMock, null, mainConnectionMock,
          account, sessionID, isHost, isLive, primaryTabPane, anchorPaneMultiViewVideo,
          anchorPaneMultiViewPresentation, anchorPaneMultiViewWhiteboard, anchorPaneVideo,
          webcamHolderOne, webcamHolderTwo, textChatHolder, anchorPanePresentation,
          anchorPaneWhiteboard, masterPane, resizePane, streamButton, disconnectButton,
          resetStream);

    }

    @Test
    public void doInitialiseAsTutorHost() {
      initialiseAsTutorHost();
    }

    @Test
    public void doChangeStreamState() {
      changeStreamState();
    }
  }

  @Nested
  class MessageManagerTest extends application.model.managers.MessageManagerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      textChat = new VBox();
      textChatScrollPane = new ScrollPane();

      messageManager = new MessageManager(textChat, textChatScrollPane);

    }

    @Test
    public void doAddMessageTest() {
      addMessageTest();
    }
  }

  @Nested
  class PresentationWindowTest extends PresentationWindowControllerTest {

    @BeforeEach
    public void setUp() {
      initMocks(this);

      prevSlideButton = new Button();
      nextSlideButton = new Button();
      loadPresentationButton = new Button();
      urlBox = new TextField();
      messageBox = new TextField();
      pane = new StackPane();
      presentationGrid = new GridPane();
      controlPane = new Pane();
    }

    @Test
    public void doInitialiseAsTutorTest() {
      initialiseAsTutorTest();
    }

    @Test
    public void doInitialiseAsUserTest() {
      initialiseAsUserTest();
    }

    @Test
    public void doSlideChangeTest() {
      slideChangeTest();
    }

    @Test
    public void doDisplayFileTest() {
      displayFileTest();
    }
  }
}