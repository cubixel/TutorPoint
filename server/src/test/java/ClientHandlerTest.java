import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static services.ServerTools.packageClass;

import application.controller.services.TextChatSession;
import application.controller.services.WhiteboardSession;
import application.model.Account;
import application.model.Message;
import application.model.PresentationRequest;
import application.model.requests.FollowSubjectRequest;
import application.model.requests.FollowTutorRequest;
import application.model.requests.SessionRequest;
import application.model.requests.SubjectRequestHome;
import application.model.requests.SubjectRequestSubscription;
import application.model.requests.TopTutorsRequest;
import application.model.requests.UpdateStreamStatusRequest;
import application.model.updates.AccountUpdate;
import application.model.updates.RatingUpdate;
import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.FileUploadResult;
import services.enums.FollowSubjectResult;
import services.enums.FollowTutorResult;
import services.enums.RatingUpdateResult;
import services.enums.SessionRequestResult;
import services.enums.StreamingStatusUpdateResult;
import services.enums.SubjectRequestResult;
import services.enums.TextChatMessageResult;
import services.enums.TutorRequestResult;
import services.enums.WhiteboardRenderResult;
import sql.MySql;

/**
 * This is the test class for the ClientHandler. It tests
 * all methods within the ClientHandler and its responses
 * to all possible request received via its DataInputStream.
 *
 * @author James Gardner
 */
public class ClientHandlerTest {

  private ClientHandler clientHandler;
  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByClientHandler;
  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;
  private final String username = "someUsername";
  private final String repeatUsername = "someRepeatUsername";
  private final String emailAddress = "someEmail";
  private final String hashedpw = "somePassword";
  private final int userID = 1;
  private final int tutorStatus = 1;
  private final int isRegister = 1;

  private static final Logger log = LoggerFactory.getLogger("ClientHandlerTest");

  @Mock
  private MySql mySqlMock;

  @Mock
  private ResultSet resultSetMock;

  @Mock
  private MainServer mainServerMock;

  @Mock
  private SessionFactory sessionFactoryMock;

  @Mock
  private Session sessionMock;

  @Mock
  private ClientNotifier clientNotifierMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> loggedInClientsMock;

  @Mock
  private ClientHandler clientHandlerMock;

  @Mock
  private WhiteboardHandler whiteboardHandlerMock;

  @Mock
  private TextChatHandler textChatHandlerMock;

  @Mock
  private PresentationHandler presentationHandlerMock;

  /**
   * Used to create the DataInput/OutputStreams used
   * to communicate between the test and the ClientHandler.
   *
   */
  @BeforeEach
  public void setUp() {
    log.debug("Initialising setup...");
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
      dosToBeWrittenTooByClientHandler = new DataOutputStream(new PipedOutputStream(pipeInputTwo));
    } catch (IOException e) {
      fail(e);
    }

    clientHandler = new ClientHandler(disReceivingDataFromTest,
        dosToBeWrittenTooByClientHandler, 1, mySqlMock, mainServerMock, sessionFactoryMock,
        sessionMock, clientNotifierMock);
    clientHandler.start();
    log.debug("Setup complete, running test");
  }

  /**
   * METHOD DESCRIPTION.
   *
   * @throws IOException DESCRIPTION
   */
  @AfterEach
  public void cleanUp() throws IOException {
    disForTestToReceiveResponse.close();
    dosToBeWrittenTooByClientHandler.close();
    disReceivingDataFromTest.close();
    dosToBeWrittenTooByTest.close();
  }

  @Test
  public void pingTest() throws IOException {
    assertTrue(clientHandler.isAlive());
    dosToBeWrittenTooByTest.writeUTF("Ping");
    String result = listenForString();
    assertEquals("Ping", result);

  }

  @Test
  public void heartbeatTest() throws InterruptedException {
    when(mainServerMock.getAllClients()).thenReturn(loggedInClientsMock);
    when(mainServerMock.getAllClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.remove(0, clientHandler)).thenReturn(true);

    assertTrue(clientHandler.isAlive());
    Thread.sleep(11000);
    assertFalse(clientHandler.isAlive());
  }

  @Test
  public void registerNewAccount() throws IOException {
    when(mySqlMock.usernameExists(username)).thenReturn(false);
    when(mySqlMock.createAccount(username, emailAddress, hashedpw, tutorStatus)).thenReturn(true);

    Account testAccount =
        new Account(userID, username, emailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));

    String result = listenForString();
    assertEquals(AccountRegisterResult.ACCOUNT_REGISTER_SUCCESS,
        new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void registerRepeatUsername() throws IOException {
    when(mySqlMock.usernameExists(repeatUsername)).thenReturn(true);
    Account testAccount =
        new Account(userID, repeatUsername, emailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountRegisterResult.FAILED_BY_USERNAME_TAKEN,
        new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void registerRepeatEmail() throws IOException {
    String repeatEmailAddress = "somerRepeatEmail";
    when(mySqlMock.emailExists(repeatEmailAddress)).thenReturn(true);
    Account testAccount =
        new Account(userID, username, repeatEmailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountRegisterResult.FAILED_BY_EMAIL_TAKEN,
        new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void loginUserTest() throws IOException {
    when(mySqlMock.checkUserDetails(repeatUsername, hashedpw)).thenReturn(false);
    Account testAccount = new Account(repeatUsername, hashedpw);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals("{\"userID\":0,\"username\":\"someRepeatUsername\",\"hashedpw\":"
        + "\"somePassword\",\"tutorStatus\":0,\"isRegister\":0,\"rating\":0.0,\""
        + "followedSubjects\":[],\"Class\":\"Account\"}", result);
    result = listenForString();
    assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS,
        new Gson().fromJson(result, AccountLoginResult.class));

    try {
      when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
      when(mySqlMock.getUserID(username)).thenReturn(userID);
      when(mySqlMock.getEmailAddress(userID)).thenReturn(emailAddress);
      when(mySqlMock.getTutorStatus(userID)).thenReturn(tutorStatus);
      when(mySqlMock.getFavouriteSubjects(userID)).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(false);
      when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
      when(loggedInClientsMock.putIfAbsent(userID, clientHandler)).thenReturn(null);
      testAccount = new Account(username, hashedpw);
      dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
      result = listenForString();
      assertEquals("{\"userID\":1,\"username\":\"someUsername\",\"emailAddress\":\""
          + "someEmail\",\"hashedpw\":\"somePassword\",\"tutorStatus\":1,\"isRegister\":0,\""
          + "rating\":0.0,\"followedSubjects\":[],\"Class\":\"Account\"}", result);
      result = listenForString();
      assertEquals(AccountLoginResult.LOGIN_SUCCESS,
          new Gson().fromJson(result, AccountLoginResult.class));
    } catch (SQLException sqlException) {
      fail(sqlException);
    }
  }

  @Test
  public void updateUserDetailsFailedByCredentialsTest() {
    String incorrectPassword = "shouldNotWork";
    String usernameUpdate = "newusername";
    String emailAddressUpdate = "newemail";
    String hashedpwUpdate = "newpassword";
    int tutorStatusUpdate = 0;

    when(mySqlMock.checkUserDetails(username, incorrectPassword)).thenReturn(false);

    try {
      Account account = new Account(userID, username, emailAddress,
          incorrectPassword, tutorStatus, 0);
      AccountUpdate accountUpdate = new AccountUpdate(account, usernameUpdate, emailAddressUpdate,
          hashedpwUpdate, tutorStatusUpdate);
      dosToBeWrittenTooByTest.writeUTF(packageClass(accountUpdate));
      String result = listenForString();
      assertEquals(AccountUpdateResult.FAILED_BY_CREDENTIALS,
          new Gson().fromJson(result, AccountUpdateResult.class));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void updateUserDetailsFailedByUsernameTakenTest() {
    String usernameUpdate = "newusername";
    String emailAddressUpdate = "newemail";
    String hashedpwUpdate = "newpassword";
    int tutorStatusUpdate = 0;

    when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
    when(mySqlMock.usernameExists(usernameUpdate)).thenReturn(true);

    try {
      Account account = new Account(userID, username, emailAddress,
          hashedpw, tutorStatus, 0);
      AccountUpdate accountUpdate = new AccountUpdate(account, usernameUpdate, emailAddressUpdate,
          hashedpwUpdate, tutorStatusUpdate);
      dosToBeWrittenTooByTest.writeUTF(packageClass(accountUpdate));
      String result = listenForString();
      assertEquals(AccountUpdateResult.FAILED_BY_USERNAME_TAKEN,
          new Gson().fromJson(result, AccountUpdateResult.class));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void updateUserDetailsFailedByEmailTakenTest() {
    String usernameUpdate = "newusername";
    String emailAddressUpdate = "newemail";
    String hashedpwUpdate = "newpassword";
    int tutorStatusUpdate = 0;

    when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
    when(mySqlMock.usernameExists(usernameUpdate)).thenReturn(false);
    when(mySqlMock.emailExists(emailAddressUpdate)).thenReturn(true);

    try {
      Account account = new Account(userID, username, emailAddress,
          hashedpw, tutorStatus, 0);
      AccountUpdate accountUpdate = new AccountUpdate(account, usernameUpdate, emailAddressUpdate,
          hashedpwUpdate, tutorStatusUpdate);
      dosToBeWrittenTooByTest.writeUTF(packageClass(accountUpdate));
      String result = listenForString();
      assertEquals(AccountUpdateResult.FAILED_BY_EMAIL_TAKEN,
          new Gson().fromJson(result, AccountUpdateResult.class));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void updateUserDetailsSucessTest() {
    String usernameUpdate = "newusername";
    String emailAddressUpdate = "newemail";
    String hashedpwUpdate = "newpassword";
    int tutorStatusUpdate = 0;

    when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
    when(mySqlMock.usernameExists(usernameUpdate)).thenReturn(false);
    when(mySqlMock.emailExists(emailAddressUpdate)).thenReturn(false);

    try {
      Account account = new Account(userID, username, emailAddress,
          hashedpw, tutorStatus, 0);
      AccountUpdate accountUpdate = new AccountUpdate(account, usernameUpdate, emailAddressUpdate,
          hashedpwUpdate, tutorStatusUpdate);
      dosToBeWrittenTooByTest.writeUTF(packageClass(accountUpdate));
      String result = listenForString();
      assertEquals(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS,
          new Gson().fromJson(result, AccountUpdateResult.class));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void updateRatingTest() {
    int tutorID = 4;
    int rating = 3;

    try {
      RatingUpdate ratingUpdate = new RatingUpdate(rating, userID, tutorID);
      dosToBeWrittenTooByTest.writeUTF(packageClass(ratingUpdate));
      String result = listenForString();
      assertEquals(RatingUpdateResult.RATING_UPDATE_SUCCESS,
          new Gson().fromJson(result, RatingUpdateResult.class));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void subjectRequestHomeTest() {
    int subjectsRequested = 0;
    SubjectRequestHome subjectRequestHome = new SubjectRequestHome(subjectsRequested, userID);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(subjectRequestHome));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS,
          new Gson().fromJson(result, SubjectRequestResult.class));
      verify(clientNotifierMock, times(1)).sendSubjects(mySqlMock,
          subjectsRequested, null, userID, "Home");
    } catch (IOException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void subjectRequestSubscriptionTest() {
    int subjectsRequested = 0;
    String subject = "TestSubject";
    SubjectRequestSubscription subjectRequestSubscription = new SubjectRequestSubscription(
        subjectsRequested, userID, subject);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(subjectRequestSubscription));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS,
          new Gson().fromJson(result, SubjectRequestResult.class));
      verify(clientNotifierMock, times(1)).sendSubjects(mySqlMock,
          subjectsRequested, subject, userID, "Subscriptions");
    } catch (IOException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void topTutorsRequestTest() {
    int tutorsRequested = 0;
    TopTutorsRequest topTutorsRequest = new TopTutorsRequest(tutorsRequested, userID);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(topTutorsRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(TutorRequestResult.TUTOR_REQUEST_SUCCESS,
          new Gson().fromJson(result, TutorRequestResult.class));
      verify(clientNotifierMock, times(1)).sendTopTutors(mySqlMock,
          tutorsRequested, userID);
    } catch (IOException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void sessionRequestHostStartingSessionTest() {
    int sessionID = userID;

    when(sessionFactoryMock.createSession(sessionID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(false);

    SessionRequest sessionRequest = new SessionRequest(userID, sessionID,
        true, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_SESSION_SETUP,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(sessionFactoryMock.createSession(sessionID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(true);

    sessionRequest = new SessionRequest(userID, sessionID,
        true, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    verify(sessionMock, times(2)).setUp();
  }

  @Test
  public void sessionRequestHostLeavingSessionTest() {
    int sessionID = userID;

    when(sessionFactoryMock.createSession(sessionID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(false);

    SessionRequest sessionRequest = new SessionRequest(userID, sessionID,
        true, true);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.END_SESSION_REQUEST_SUCCESS,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    verify(sessionMock, times(1)).stopWatching(userID, clientHandler);
  }

  @Test
  public void sessionRequestUserJoiningSessionTest() {
    /* For a user to join a session they must be logged in*/
    try {
      when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
      when(mySqlMock.getUserID(username)).thenReturn(userID);
      when(mySqlMock.getEmailAddress(userID)).thenReturn(emailAddress);
      when(mySqlMock.getTutorStatus(userID)).thenReturn(tutorStatus);
      when(mySqlMock.getFavouriteSubjects(userID)).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(false);
      when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
      when(loggedInClientsMock.putIfAbsent(userID, clientHandler)).thenReturn(null);
      Account testAccount = new Account(username, hashedpw);
      dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      listenForString();
      listenForString();
    } catch (SQLException | IOException | InterruptedException sqlException) {
      fail(sqlException);
    }

    int sessionID = 314;

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.containsKey(sessionID)).thenReturn(false);

    SessionRequest sessionRequest = new SessionRequest(userID, sessionID,
        false, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.containsKey(sessionID)).thenReturn(true);

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(sessionID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(false);

    sessionRequest = new SessionRequest(userID, sessionID,
        false, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.containsKey(sessionID)).thenReturn(true);

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(sessionID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(true);

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(sessionID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);

    sessionRequest = new SessionRequest(userID, sessionID,
        false, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    verify(sessionMock, times(1)).requestJoin(userID);
  }

  @Test
  public void whiteboardSessionTest() {
    /* Setting up a session as Host for WhiteboardSessionTest */
    when(sessionFactoryMock.createSession(userID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(true);

    SessionRequest sessionRequest = new SessionRequest(userID, userID,
        true, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(false);

    WhiteboardSession whiteboardSession = new WhiteboardSession(userID, userID);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(whiteboardSession));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(WhiteboardRenderResult.FAILED_BY_CREDENTIALS,
          new Gson().fromJson(result, WhiteboardRenderResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(true);
    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getWhiteboardHandler()).thenReturn(whiteboardHandlerMock);

    whiteboardSession = new WhiteboardSession(userID, userID);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(whiteboardSession));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(WhiteboardRenderResult.WHITEBOARD_RENDER_SUCCESS,
          new Gson().fromJson(result, WhiteboardRenderResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    verify(whiteboardHandlerMock, times(1)).addToQueue(any());
  }

  @Test
  public void textChatSessionTest() {
    /* Setting up a session as Host for WhiteboardSessionTest */
    when(sessionFactoryMock.createSession(userID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(true);

    SessionRequest sessionRequest = new SessionRequest(userID, userID,
        true, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(false);

    Message message = new Message(username, userID, userID, "This is a test message");

    TextChatSession textChatSession = new TextChatSession(username, userID, userID, message);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(textChatSession));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(TextChatMessageResult.FAILED_BY_INCORRECT_USER_ID,
          new Gson().fromJson(result, TextChatMessageResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.isLive()).thenReturn(true);
    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(clientHandlerMock);
    when(clientHandlerMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getTextChatHandler()).thenReturn(textChatHandlerMock);

    textChatSession = new TextChatSession(username, userID, userID, message);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(textChatSession));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS,
          new Gson().fromJson(result, TextChatMessageResult.class));
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    verify(textChatHandlerMock, times(1)).addToQueue(any());
  }

  @Test
  public void presentationRequestTest() {
    when(sessionMock.getPresentationHandler()).thenReturn(presentationHandlerMock);
    when(sessionMock.getPresentationHandler()).thenReturn(presentationHandlerMock);

    PresentationRequest presentationRequest = new PresentationRequest("Test", 1);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(presentationRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      verify(presentationHandlerMock, times(1)).setRequestSlideNum(1);
      verify(presentationHandlerMock, times(1)).setRequestAction("Test");
    } catch (IOException | InterruptedException e) {
      fail(e);
    }

  }

  @Test
  public void followTutorRequestTest() {
    try {
      FollowTutorRequest followTutorRequest = new FollowTutorRequest(2, false);
      dosToBeWrittenTooByTest.writeUTF(packageClass(followTutorRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS,
          new Gson().fromJson(result, FollowTutorResult.class));
      verify(mySqlMock, times(1)).addToFollowedTutors(0, 2);
      followTutorRequest = new FollowTutorRequest(2, true);
      dosToBeWrittenTooByTest.writeUTF(packageClass(followTutorRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      result = listenForString();
      assertEquals(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS,
          new Gson().fromJson(result, FollowTutorResult.class));
      verify(mySqlMock, times(1)).removeFromFollowedTutors(0, 2);
    } catch (IOException | SQLException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void followSubjectRequestTest() {
    try {
      FollowSubjectRequest followSubjectRequest = new FollowSubjectRequest(2, false);
      dosToBeWrittenTooByTest.writeUTF(packageClass(followSubjectRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(FollowSubjectResult.FOLLOW_SUBJECT_RESULT_SUCCESS,
          new Gson().fromJson(result, FollowSubjectResult.class));
      verify(mySqlMock, times(1)).addSubjectToFavourites(2, 0);
      followSubjectRequest = new FollowSubjectRequest(2, true);
      dosToBeWrittenTooByTest.writeUTF(packageClass(followSubjectRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      result = listenForString();
      assertEquals(FollowSubjectResult.FOLLOW_SUBJECT_RESULT_SUCCESS,
          new Gson().fromJson(result, FollowSubjectResult.class));
      verify(mySqlMock, times(1)).removeFromFavouriteSubjects(0, 2);
    } catch (IOException | SQLException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void updateStreamStatusRequestTest() {
    try {
      when(sessionMock.isLive()).thenReturn(false);

      UpdateStreamStatusRequest updateStreamStatusRequest = new UpdateStreamStatusRequest(true);
      dosToBeWrittenTooByTest.writeUTF(packageClass(updateStreamStatusRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      String result = listenForString();
      assertEquals(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS,
          new Gson().fromJson(result, StreamingStatusUpdateResult.class));
      verify(mySqlMock, times(1)).startLiveSession(0, 0);
      verify(sessionMock, times(1)).setLive(true);

      when(sessionMock.isLive()).thenReturn(true);
      updateStreamStatusRequest = new UpdateStreamStatusRequest(false);
      dosToBeWrittenTooByTest.writeUTF(packageClass(updateStreamStatusRequest));
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      result = listenForString();
      assertEquals(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS,
          new Gson().fromJson(result, StreamingStatusUpdateResult.class));
      verify(mySqlMock, times(1)).endLiveSession(0, 0);
      verify(sessionMock, times(1)).kickAll();
      verify(sessionMock, times(1)).setLive(false);
    } catch (IOException | SQLException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void logoutTest() {
    try {
      String logout = "Logout";
      clientHandler.setInSession(false);
      when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
      dosToBeWrittenTooByTest.writeUTF(logout);
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      verify(sessionMock, times(1)).cleanUp();
      verify(sessionMock, times(1)).setLive(false);
      verify(mainServerMock, times(1)).updateLiveClientList();
      verify(loggedInClientsMock, times(1)).remove(0, clientHandler);

      clientHandler.setInSession(true);
      when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
      when(loggedInClientsMock.containsKey(0)).thenReturn(false);
      when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
      dosToBeWrittenTooByTest.writeUTF(logout);
      /* Letting the ClientHandler catch up */
      Thread.sleep(100);
      verify(sessionMock, times(2)).cleanUp();
      verify(sessionMock, times(2)).setLive(false);
      verify(mainServerMock, times(2)).updateLiveClientList();
      verify(loggedInClientsMock, times(1)).remove(0, clientHandler);
      assertFalse(clientHandler.isInSession());
    } catch (IOException | InterruptedException e) {
      fail(e);
    }
  }

  @Test
  public void profileImageTest() {
    try {
      dosToBeWrittenTooByTest.writeUTF("ProfileImage");

      String path = "src" + File.separator + "test" + File.separator + "resources"
          + File.separator + "services" + File.separator + "TestImage.png";

      File file = new File(path);

      byte[] byteArray = new byte[(int) file.length()];

      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      DataInputStream dis = new DataInputStream(bis);

      dis.readFully(byteArray, 0, byteArray.length);
      log.info("Sending filename '" + file.getName() + "' of size " + byteArray.length);
      dosToBeWrittenTooByTest.writeUTF(file.getName());
      dosToBeWrittenTooByTest.writeLong(byteArray.length);
      dosToBeWrittenTooByTest.write(byteArray, 0, byteArray.length);
      dosToBeWrittenTooByTest.flush();

      /* Letting the ClientHandler catch up */
      Thread.sleep(100);

      String result = listenForString();
      assertEquals(FileUploadResult.FILE_UPLOAD_SUCCESS,
          new Gson().fromJson(result, FileUploadResult.class));

    } catch (IOException | InterruptedException e) {
      fail(e);
    }

    String path = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded" + File.separator
        + "profilePictures" + File.separator + "user0profilePicture.png";

    File file = new File(path);

    if (file.delete()) {
      log.info("Clean up successful.");
    } else {
      log.warn("Clean up failed, please ensure file 'user0profilePicture.png' is deleted!");
    }

  }

  /**
   * Used to listen for a response sent from the
   * client handler. This is needed to simulate actual
   * operation.
   */
  public String listenForString() throws IOException {
    String incoming = null;
    boolean received = false;

    do {
      while (disForTestToReceiveResponse.available() > 0 && !received) {
        incoming = disForTestToReceiveResponse.readUTF();
        received = true;
      }
    } while ((incoming == null));
    return incoming;
  }
}
