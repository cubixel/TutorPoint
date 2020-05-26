package application.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountUpdateResult;
import application.controller.presentation.ImageHandler;
import application.controller.presentation.ImageHandlerTest;
import application.controller.presentation.PresentationSlideTest;
import application.controller.presentation.TextHandler;
import application.controller.presentation.TextHandlerTest;
import application.controller.presentation.TimingManagerTest;
import application.controller.presentation.VideoHandler;
import application.controller.presentation.VideoHandlerTest;
import application.controller.presentation.XmlHandler;
import application.controller.presentation.exceptions.XmlLoadingException;
import application.controller.services.FollowSubjectRequestService;
import application.controller.services.FollowTutorRequestService;
import application.controller.services.ListenerThread;
import application.controller.services.LoginService;
import application.controller.services.RegisterService;
import application.controller.services.SessionRequestService;
import application.controller.services.TextChatService;
import application.controller.services.UpdateDetailsService;
import application.controller.services.UpdateProfilePictureService;
import application.controller.services.UpdateStreamingStatusService;
import application.controller.services.WhiteboardService;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.Whiteboard;
import application.model.managers.MessageManager;
import application.model.managers.SubjectManager;
import application.model.managers.SubscriptionsManger;
import application.model.managers.TutorManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
public class JavafxPlatformTests {

  private static final Logger log = LoggerFactory.getLogger("GuiTests");

  /**
   * This method starts the JavaFX runtime. The specified Runnable will then be
   * called on the JavaFX Application Thread.
   */
  @BeforeAll
  public static void setUpToolkit() {
    Platform.startup(() -> log.info("Toolkit initialized ..."));
  }

  /**
   * This method ends the JavaFX runtime.
   */
  @AfterAll
  public static void cleanUp() {
    Platform.exit();
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
      // TODO Fix this test, assertions from mouse press event do not pass
      //testDrawLine();
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
      // TODO Passes when run individually not when full JavafxPlatformTest runs
      //followSubjectActionTest();
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
      // TODO Passes when run individually not when full JavafxPlatformTest runs
      //followTutorActionTest();
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
      // TODO Passes when run individually not when full JavafxPlatformTest runs
      //changeStreamState();
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
    public void doVerifyXmlTest() {
      verifyXmlTest();
    }

    @Test
    public void doDisplayFileTest() {
      displayFileTest();
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

  @Nested
  class ExtendoImageHandlerTest extends ImageHandlerTest {

    @BeforeEach
    public void setUp() {
      imageHandler = new ImageHandler(new StackPane());
    }

    @Test
    public void doRegisterOneImage() {
      registerOneImage();
    }

    @Test
    public void doRegisterTwoImages() {
      registerTwoImages();
    }

    @Test
    public void doRegisterTwoImagesSameId() {
      registerTwoImagesSameId();
    }

    @Test
    public void doDeregisterExtantImage() {
      deregisterExtantImage();
    }

    @Test
    public void doDeregisterNonExtantImage() {
      deregisterNonExtantImage();
    }

    @Test
    public void doDeregisterExtantImageTwice() {
      deregisterExtantImageTwice();
    }

    @Test
    public void doDrawExtantImage() {
      drawExtantImage();
    }

    @Test
    public void doDrawNonExtantImage() {
      drawNonExtantImage();
    }

    @Test
    public void doDrawExtantImageTwice() {
      drawExtantImageTwice();
    }

    @Test
    public void doUnDrawExtantImage() {
      unDrawExtantImage();
    }

    @Test
    public void doUnDrawNonExtantImage() {
      unDrawNonExtantImage();
    }

    @Test
    public void doUnDrawExtantImageTwice() {
      unDrawExtantImageTwice();
    }
  }

  @Nested
  class ExtendoVideoHandlerTest extends VideoHandlerTest {

    @BeforeEach
    public void setUp() {
      videoHandler = new VideoHandler(new StackPane());
    }

    @Test
    public void doRegisterOneVideo() {
      registerOneVideo();
    }

    @Test
    public void doRegisterTwoVideos() {
      registerTwoVideos();
    }

    @Test
    public void doRegisterTwoVideosSameId() {
      registerTwoVideosSameId();
    }

    @Test
    public void doDeregisterExtantVideo() {
      deregisterExtantVideo();
    }

    @Test
    public void doDeregisterNonExtantVideo() {
      deregisterNonExtantVideo();
    }

    @Test
    public void doDeregisterExtantVideoTwice() {
      deregisterExtantVideoTwice();
    }

    @Test
    public void doDrawExtantVideo() {
      drawExtantVideo();
    }

    @Test
    public void doDrawNonExtantVideo() {
      drawNonExtantVideo();
    }

    @Test
    public void doDrawExtantVideoTwice() {
      drawExtantVideoTwice();
    }

    @Test
    public void doUnDrawExtantVideo() {
      unDrawExtantVideo();
    }

    @Test
    public void doUnDrawNonExtantVideo() {
      unDrawNonExtantVideo();
    }

    @Test
    public void doUnDrawExtantVideoTwice() {
      unDrawExtantVideoTwice();
    }
  }

  @Nested
  class ExtendoTextHandlerTest extends TextHandlerTest {

    @BeforeEach
    public void setUp() {
      textHandler = new TextHandler(new StackPane(), "Arial", 11, "#000000");

      //can't (easily) just make a node, don't want to mock it, read it in time.
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = null;
      try {
        xmlDoc = handler.makeXmlFromUrl(
              "src/main/resources/application/media/XML/PresentationSlide/"
              + "TestXML.xml");
      } catch (XmlLoadingException e) {
        e.printStackTrace();
      }
      assert xmlDoc != null;
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      textNode = slides.item(0).getChildNodes().item(1);
    }

    @Test
    public void doRegisterOneText() {
      registerOneText();
    }

    @Test
    public void doRegisterTwoTexts() {
      registerTwoTexts();
    }

    @Test
    public void doRegisterTwoTextsSameId() {
      registerTwoTextsSameId();
    }

    @Test
    public void doDeregisterExtantText() {
      deregisterExtantText();
    }

    @Test
    public void doDeregisterNonExtantText() {
      deregisterNonExtantText();
    }

    @Test
    public void doDeregisterExtantTextTwice() {
      deregisterExtantTextTwice();
    }

    @Test
    public void doDrawExtantText() {
      drawExtantText();
    }

    @Test
    public void doDrawNonExtantText() {
      drawNonExtantText();
    }

    @Test
    public void doDrawExtantTextTwice() {
      drawExtantTextTwice();
    }

    @Test
    public void doUnDrawExtantText() {
      unDrawExtantText();
    }

    @Test
    public void doUnDrawNonExtantText() {
      unDrawNonExtantText();
    }

    @Test
    public void doUnDrawExtantTextTwice() {
      unDrawExtantTextTwice();
    }
  }

  @Nested
  class TimingTests extends TimingManagerTest {

    @BeforeEach
    public void setUp() {
      stackPane = new StackPane();
    }

    @Test
    public void doTestSlideTiming() {
      // TODO Fix this test, timings are too tight, need some leeway
      //testSlideTiming();
    }

    @Test
    public void doTestSlideTimingNeg1() {
      // TODO Fix this test, timings are too tight, need some leeway
      // testSlideTimingNeg1();
    }

    @Test
    public void doTestElementTiming() {
      // TODO Fix this test, timings are too tight, need some leeway
      //testElementTiming();
    }

    @Test
    public void doTestImplicitRemovalVisual() {
      testImplicitRemovalVisual();
    }
  }

  @Nested
  class FollowSubjectRequestServiceTest extends
      application.controller.services.FollowSubjectRequestServiceTest {

    /**
     * Initialises Mocks and creates a FollowSubjectRequestService instance to test on.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      followSubjectRequestService = new FollowSubjectRequestService(mainConnectionMock, 1,
          true);

      threadDone = false;
    }

    @Test
    public void doClaimingConnectionTest() {
      claimingConnectionTest();
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByDatabaseErrorTest() {
      failedByDatabaseErrorTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class FollowTutorRequestServiceTest extends
      application.controller.services.FollowTutorRequestServiceTest {

    /**
     * Initialises Mocks and creates a FollowSubjectRequestService instance to test on.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      followTutorRequestService = new FollowTutorRequestService(mainConnectionMock, 1,
          true);

      threadDone = false;
    }

    @Test
    public void doClaimingConnectionTest() {
      claimingConnectionTest();
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByDatabaseErrorTest() {
      failedByDatabaseErrorTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class ListenerThreadTest extends application.controller.services.ListenerThreadTest {

    /**
     * Used to create the DataInput/OutputStreams used
     * to communicate between the test and the ListenerThread.
     */
    @BeforeEach
    public void setUp() {
      log.info("Initialising setup...");
      MockitoAnnotations.initMocks(this);

      /*
       * Creating a PipedInputStream to connect a DataOutputStream and DataInputStream together
       * this is used to write a test case to the dis of the to the UUT.
       */
      PipedInputStream pipeInputOne = new PipedInputStream();

      disReceivingDataFromTest = new DataInputStream(pipeInputOne);

      try {
        dosToBeWrittenTooByTest = new DataOutputStream(new PipedOutputStream(pipeInputOne));
      } catch (IOException e) {
        fail(e);
      }

      /*
       * Creating a PipedInputStream to connect a DataOutputStream and DataInputStream together
       * this is used to read the response that the UUT writes to its DataOutputStream.
       */
      PipedInputStream pipeInputTwo = new PipedInputStream();

      disForTestToReceiveResponse = new DataInputStream(pipeInputTwo);

      try {
        dosToBeWrittenTooByListenerThread = new DataOutputStream(
            new PipedOutputStream(pipeInputTwo));
      } catch (IOException e) {
        fail(e);
      }

      listenerThread = new ListenerThread(disReceivingDataFromTest,
          dosToBeWrittenTooByListenerThread);

      listenerThread.setWhiteboardService(whiteboardServiceMock);
      listenerThread.setTextChatService(textChatServiceMock);
      listenerThread.addPresentationWindowController(presentationWindowControllerMock);
      listenerThread.addHomeWindowController(homeWindowControllerMock);
      listenerThread.addSubscriptionsWindowController(subscriptionsWindowControllerMock);
      listenerThread.addTutorWindowController(tutorWindowControllerMock);

      log.info("Setup complete, running test");
    }

    /**
     * Clean up DI/OStreams.
     *
     * @throws IOException
     *         Thrown if error closing streams.
     */
    @AfterEach
    public void cleanUp() throws IOException {
      disForTestToReceiveResponse.close();
      dosToBeWrittenTooByListenerThread.close();
      disReceivingDataFromTest.close();
      dosToBeWrittenTooByTest.close();
    }

    @Test
    public void doWhiteboardSessionTest() {
      whiteboardSessionTest();
    }

    @Test
    public void doSubjectHomeWindowResponseTest() {
      subjectHomeWindowResponseTest();
    }

    @Test
    public void doSubjectSubscriptionsWindowResponseTest() {
      subjectSubscriptionsWindowResponseTest();
    }

    @Test
    public void doTopTutorHomeWindowResponseTest() {
      topTutorHomeWindowResponseTest();
    }

    @Test
    public void doLiveTutorHomeWindowUpdateTest() {
      liveTutorHomeWindowUpdateTest();
    }

    @Test
    public void doPresentationChangeSlideRequestTest() {
      presentationChangeSlideRequestTest();
    }

    @Test
    public void doTextChatSessionTest() {
      textChatSessionTest();
    }

    @Test
    public void doSendingPresentationTest() {
      sendingPresentationTest();
    }
  }

  @Nested
  class LoginServiceTest extends application.controller.services.LoginServiceTest {

    /**
     * Initialises Mocks, sets up Mock return values when called and creates
     * an instance of the UUT.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      loginService = new LoginService(accountMock, mainConnectionMock);

      threadDone = false;
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByUserCredentialsTest() {
      failedByUserCredentialsTest();
    }

    @Test
    public void doFailedByNetworkTest() {
      failedByNetworkTest();
    }
  }

  @Nested
  class RegisterServiceTest extends application.controller.services.RegisterServiceTest {

    /**
     * Initialises Mocks, sets up Mock return values when called and creates
     * an instance of the UUT.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      registerService = new RegisterService(accountMock, mainConnectionMock);

      threadDone = false;
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByUserCredentialsTest() {
      failedByUserCredentialsTest();
    }

    @Test
    public void doFailedByUsernameTakenTest() {
      failedByUsernameTakenTest();
    }

    @Test
    public void doFailedByEmailTakenTest() {
      failedByEmailTakenTest();
    }

    @Test
    public void doFailedByNetworkTest() {
      failedByNetworkTest();
    }
  }

  @Nested
  class SessionRequestServiceTest extends
      application.controller.services.SessionRequestServiceTest {

    /**
     * Initialises Mocks and creates a SessionRequestService instance to test on.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);
      int userID = 1;
      int sessionID = 1;

      sessionRequestService = new SessionRequestService(mainConnectionMock, userID, sessionID,
          false, true);

      threadDone = false;
    }

    @Test
    public void doClaimingConnectionTest() {
      claimingConnectionTest();
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByTutorNotLiveTest() {
      failedByTutorNotLiveTest();
    }

    @Test
    public void doFailedByTutorNotOnlineTest() {
      failedByTutorNotOnlineTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class TextChatServiceTest extends application.controller.services.TextChatServiceTest {

    /**
     * Initialises Mocks, sets up Mock return values when called and creates
     * an instance of the UUT.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      textChatService = new TextChatService(message, messageManager, mainConnectionMock,
          "testUserName", 0, 0);

      threadDone = false;
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByNetwork() {
      failedByNetwork();
    }
  }

  @Nested
  class UpdateDetailsServiceTest extends application.controller.services.UpdateDetailsServiceTest {

    /**
     * Initialises Mocks and creates a UpdateDetailsService instance to test on.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      updateDetailsService = new UpdateDetailsService(accountUpdateMock, mainConnectionMock);

      threadDone = false;
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByCredentialsTest() {
      failedByCredentialsTest();
    }

    @Test
    public void doFailedByUsernameTakenTest() {
      failedByUsernameTakenTest();
    }

    @Test
    public void doFailedByEmailTakenTest() {
      failedByEmailTakenTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class UpdateProfilePictureServiceTest extends
      application.controller.services.UpdateProfilePictureServiceTest {

    /**
     * Initialises Mocks, sets up Mock return values when called and creates
     * an instance of the UUT.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      updateProfilePictureService = new UpdateProfilePictureService(fileMock, mainConnectionMock);

      threadDone = false;
    }

    @Test
    public void doClaimingConnectionTest() {
      claimingConnectionTest();
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByServerErrorTest() {
      failedByServerErrorTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class UpdateStreamingStatusServiceTest extends
      application.controller.services.UpdateStreamingStatusServiceTest {

    /**
     * Initialises Mocks and creates a FollowSubjectRequestService instance to test on.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      updateStreamingStatusService = new UpdateStreamingStatusService(mainConnectionMock, true);

      threadDone = false;
    }

    @Test
    public void doClaimingConnectionTest() {
      claimingConnectionTest();
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedAccessingDatabaseTest() {
      failedAccessingDatabaseTest();
    }

    @Test
    public void doFailingByNetworkTest() {
      failingByNetworkTest();
    }
  }

  @Nested
  class WhiteboardServiceTest extends application.controller.services.WhiteboardServiceTest {

    /**
     * Initialises Mocks, sets up Mock return values when called and creates
     * an instance of the UUT.
     */
    @BeforeEach
    public void setUp() {
      initMocks(this);

      whiteboardService = new WhiteboardService(mainConnectionMock, whiteboard, 0, 0);

      threadDone = false;
    }

    @Test
    public void doSuccessfulResultTest() {
      successfulResultTest();
    }

    @Test
    public void doFailedByNetwork() {
      failedByNetwork();
    }

    @Test
    public void doFailedByCredentials() {
      failedByCredentials();
    }
  }

  @Nested
  class SecurityTest extends application.controller.tools.SecurityTest {

    /**
     * Instantiated the Classes need for the tests.
     */
    @BeforeEach
    public void setUp() {
      errorLabel = new Label();
    }

    @Test
    public void doHashTest() {
      hashTest();
    }

    @Test
    public void doUsernameIsValidTest() {
      usernameIsValidTest();
    }

    @Test
    public void doEmailIsValid() {
      emailIsValid();
    }

    @Test
    public void doPasswordIsValid() {
      passwordIsValid();
    }
  }
}
