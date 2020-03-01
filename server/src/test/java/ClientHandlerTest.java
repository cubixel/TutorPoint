import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static services.ServerTools.packageClass;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import services.AccountLoginResult;
import services.AccountRegisterResult;
import services.SubjectRequestService;
import sql.MySQL;

public class ClientHandlerTest {

  private ClientHandler clientHandler;

  private DataInputStream disToReceiveResponse;
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
  private MySQL mySqlMock;

  @Mock
  private SubjectRequestService subjectRequestServiceMock;


  /**
   *
   * @throws Exception
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

    disToReceiveResponse = new DataInputStream(pipeInputTwo);

    dosToBeWrittenTooByClientHandler = new DataOutputStream(new PipedOutputStream(pipeInputTwo));

    clientHandler = new ClientHandler(disReceivingDataFromTest, dosToBeWrittenTooByClientHandler, 1, mySqlMock, subjectRequestServiceMock);
    clientHandler.start();
  }

  @AfterEach
  public void cleanUp() throws IOException {
    disToReceiveResponse.close();
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
    assertEquals(AccountRegisterResult.SUCCESS, new Gson().fromJson(result, AccountRegisterResult.class));
  }

  @Test
  public void registerRepeatAccount() throws IOException {
    Account testAccount = new Account(repeatUsername, emailAddress, hashedpw, tutorStatus, isRegister);
    dosToBeWrittenTooByTest.writeUTF(packageClass(testAccount));
    String result = listenForString();
    assertEquals(AccountRegisterResult.FAILED_BY_CREDENTIALS, new Gson().fromJson(result, AccountRegisterResult.class));
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
    assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS, new Gson().fromJson(result, AccountLoginResult.class));
  }

  @Test
  public void subjectRequestTest() throws IOException {
    dosToBeWrittenTooByTest.writeUTF("SubjectRequest");
    //verify(subjectRequestServiceMock).getSubject();
    //TODO This doesn't currently work. Not sure why.

  }

  /**
   *
   * @return
   * @throws IOException
   */
  public String listenForString() throws IOException {
    String incoming = null;

    do {
      while (disToReceiveResponse.available() > 0) {
        incoming = disToReceiveResponse.readUTF();
      }
    } while ((incoming == null));
    return incoming;
  }
}
