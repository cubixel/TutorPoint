import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static services.ServerTools.packageClass;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import model.Account;
import model.SubjectRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.SubjectRequestResult;
import sql.MySql;

public class ClientHandlerTest {

  private ClientHandler clientHandler;

  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByClientHandler;

  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;

  private String username = "someUsername";
  private String repeatUsername = "someRepeatUsername";
  private String emailAddress = "someEmail";
  private String hashedpw = "somePassword";
  private int tutorStatus = 1;
  private int isRegister = 1;
  private int isLogin = 0;


  @Mock
  private MySql mySqlMock;

  /**
   * METHOD DESCRIPTION.
   *
   * @throws Exception DESCRIPTION
   */
  @BeforeEach
  public void setUp() throws Exception {
    initMocks(this);
    when(mySqlMock.getUserDetails(username)).thenReturn(false);
    when(mySqlMock.getUserDetails(repeatUsername)).thenReturn(true);

    when(mySqlMock.checkUserDetails(username, hashedpw)).thenReturn(true);
    when(mySqlMock.checkUserDetails(repeatUsername, hashedpw)).thenReturn(false);

    when(mySqlMock.createAccount(username, emailAddress, hashedpw, tutorStatus)).thenReturn(true);

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

    clientHandler =
        new ClientHandler(disReceivingDataFromTest, dosToBeWrittenTooByClientHandler, 1, mySqlMock);
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
    assertTrue(clientHandler.isAlive());
    Thread.sleep(11000);
    assertFalse(clientHandler.isAlive());
  }

  @Test
  public void registerNewAccount() throws IOException {
    Account testAccount = new Account(username, emailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountRegisterResult.SUCCESS,
        new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void registerRepeatAccount() throws IOException {
    Account testAccount =
        new Account(repeatUsername, emailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountRegisterResult.FAILED_BY_CREDENTIALS,
        new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void loginUserTest() throws IOException {
    Account testAccount = new Account(username, emailAddress, hashedpw, tutorStatus, isLogin);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountLoginResult.SUCCESS, new Gson().fromJson(result, AccountLoginResult.class));

    testAccount = new Account(repeatUsername, emailAddress, hashedpw, tutorStatus, isLogin);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    result = listenForString();
    assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS,
        new Gson().fromJson(result, AccountLoginResult.class));
  }

  @Test
  public void subjectRequestTest() throws IOException {
    SubjectRequest subjectRequest = new SubjectRequest(1);
    dosToBeWrittenTooByTest.writeUTF(packageClass(subjectRequest));
    String result = listenForString();
    assertEquals(SubjectRequestResult.SUCCESS,
        new Gson().fromJson(result, SubjectRequestResult.class));
  }

  /* @Test
  public void subjectRequestTest() throws IOException {
    assertTrue(clientHandler.isAlive());
    dosToBeWrittenTooByTest.writeUTF("SubjectRequest");
    //verify(subjectRequestServiceMock).getSubject();
    // TODO Not Working, only seems to enter when reading from disForTestToReceiveResponse???
  } */

  /**
   * METHOD DESCRIPTION.
   */
  public String listenForString() throws IOException {
    String incoming = null;

    do {
      while (disForTestToReceiveResponse.available() > 0) {
        incoming = disForTestToReceiveResponse.readUTF();
      }
    } while ((incoming == null));
    return incoming;
  }
}
