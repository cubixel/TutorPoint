import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static services.ServerTools.packageClass;

import com.google.gson.Gson;
import externalclassesfortests.AccountUpdate;
import externalclassesfortests.RatingUpdate;
import externalclassesfortests.SessionRequest;
import externalclassesfortests.SubjectRequestHome;
import externalclassesfortests.SubjectRequestSubscription;
import externalclassesfortests.TopTutorsRequest;
import externalclassesfortests.WhiteboardSession;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import services.ClientNotifier;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.RatingUpdateResult;
import services.enums.SessionRequestResult;
import services.enums.SubjectRequestResult;
import services.enums.TutorRequestResult;
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

  /**
   * METHOD DESCRIPTION.
   *
   * @throws Exception DESCRIPTION
   */
  @BeforeEach
  public void setUp() throws Exception {
    initMocks(this);

    /*
     * Creating a PipedInputStream to connect a DataOutputStream and DataInputStream together
     * this is used to write a test case to the dis of the to the UUT.
     */
    PipedInputStream pipeInputOne = new PipedInputStream();

    disReceivingDataFromTest = new DataInputStream(pipeInputOne);

    dosToBeWrittenTooByTest = new DataOutputStream(new PipedOutputStream(pipeInputOne));

    /*
     * Creating a PipedInputStream to connect a DataOutputStream and DataInputStream together
     * this is used to read the response that the UUT writes to its DataOutputStream.
     */
    PipedInputStream pipeInputTwo = new PipedInputStream();

    disForTestToReceiveResponse = new DataInputStream(pipeInputTwo);

    dosToBeWrittenTooByClientHandler = new DataOutputStream(new PipedOutputStream(pipeInputTwo));

    clientHandler = new ClientHandler(disReceivingDataFromTest,
        dosToBeWrittenTooByClientHandler, 1, mySqlMock, mainServerMock, sessionFactoryMock,
        sessionMock);
    clientHandler.setNotifier(clientNotifierMock);
    clientHandler.start();
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
      String result = listenForString();
      assertEquals(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS,
          new Gson().fromJson(result, SubjectRequestResult.class));
      verify(clientNotifierMock, times(1)).sendSubjects(mySqlMock,
          subjectsRequested, null, userID, "Home");
    } catch (IOException e) {
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
      String result = listenForString();
      assertEquals(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS,
          new Gson().fromJson(result, SubjectRequestResult.class));
      verify(clientNotifierMock, times(1)).sendSubjects(mySqlMock,
          subjectsRequested, subject, userID, "Subscriptions");
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void topTutorsRequestTest() {
    int tutorsRequested = 0;
    TopTutorsRequest topTutorsRequest = new TopTutorsRequest(tutorsRequested, userID);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(topTutorsRequest));
      String result = listenForString();
      assertEquals(TutorRequestResult.TUTOR_REQUEST_SUCCESS,
          new Gson().fromJson(result, TutorRequestResult.class));
      verify(clientNotifierMock, times(1)).sendTopTutors(mySqlMock,
          tutorsRequested, userID);
    } catch (IOException e) {
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
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_SESSION_SETUP,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
      fail(e);
    }

    when(sessionFactoryMock.createSession(sessionID, clientHandler)).thenReturn(sessionMock);
    when(sessionMock.setUp()).thenReturn(true);

    sessionRequest = new SessionRequest(userID, sessionID,
        true, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
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
      String result = listenForString();
      assertEquals(SessionRequestResult.END_SESSION_REQUEST_SUCCESS,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
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
      listenForString();
      listenForString();
    } catch (SQLException | IOException sqlException) {
      fail(sqlException);
    }

    int sessionID = 314;

    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.containsKey(sessionID)).thenReturn(false);

    SessionRequest sessionRequest = new SessionRequest(userID, sessionID,
        false, false);
    try {
      dosToBeWrittenTooByTest.writeUTF(packageClass(sessionRequest));
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
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
      String result = listenForString();
      assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
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
      String result = listenForString();
      assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE,
          new Gson().fromJson(result, SessionRequestResult.class));
    } catch (IOException e) {
      fail(e);
    }

    verify(sessionMock, times(1)).requestJoin(userID);
  }

  @Test
  public void whiteboardSessionTest() {
//    int sessionID = 314;
//    WhiteboardSession whiteboardSession = new WhiteboardSession(userID, sessionID);
//    try {
//      dosToBeWrittenTooByTest.writeUTF(packageClass(whiteboardSession));
//      String result = listenForString();
//      assertEquals(TutorRequestResult.TUTOR_REQUEST_SUCCESS,
//          new Gson().fromJson(result, TutorRequestResult.class));
//      verify(clientNotifierMock, times(1)).sendTopTutors(mySqlMock,
//          tutorsRequested, userID);
//    } catch (IOException e) {
//      fail(e);
//    }
  }

  @Test
  public void textChatSessionTest() {
    // TODO Complete Test
  }

  @Test
  public void presentationRequestTest() {
    // TODO Complete Test
  }

  @Test
  public void followTutorRequestTest() {
    // TODO Complete Test
  }

  @Test
  public void followSubjectRequestTest() {
    // TODO Complete Test
  }

  @Test
  public void updateStreamStatusRequestTest() {
    // TODO Complete Test
  }

  @Test
  public void logoutTest() {
    // TODO Complete Test
  }

  @Test
  public void profileImageTest() {
    // TODO Complete Test
  }

  /**
   * METHOD DESCRIPTION.
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
