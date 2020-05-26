package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;

public class ClientNotifierTest {

  private ClientNotifier clientNotifier;

  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByMainConnection;
  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;

  private static final Logger log = LoggerFactory.getLogger("ClientNotifierTest");

  @Mock
  private MySql mySqlMock;

  @Mock
  private ResultSet resultSetMock;

  /**
   * This creates two connected Data Input and Output Streams so that
   * data can be written too and recieved from the MainConnection by the
   * tests.

   */
  @BeforeEach
  public void setUp() {
    initMocks(this);
    try {
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

      dosToBeWrittenTooByMainConnection = new DataOutputStream(new PipedOutputStream(pipeInputTwo));

      clientNotifier = new ClientNotifier(disReceivingDataFromTest,
          dosToBeWrittenTooByMainConnection);
    } catch (IOException e) {
      fail();
      log.error("Could not setup DIS and DOS", e);
    }
  }

  /**
   * Closing the DIS and DOS between each test.
   */
  @AfterEach
  public void cleanUp() {
    try {
      disForTestToReceiveResponse.close();
      dosToBeWrittenTooByMainConnection.close();
      disReceivingDataFromTest.close();
      dosToBeWrittenTooByTest.close();
    } catch (IOException e) {
      fail();
      log.error("Could not close DIS and DOS", e);
    }
  }

  @Test
  public void sendClassTest() {
    Account account = new Account("username", "password");
    String expected = "{\"userID\":0,\"username\":\"username\",\"hashedpw\":\"password\","
        + "\"tutorStatus\":0,\"isRegister\":0,\"rating\":0.0,\"followedSubjects\":[],"
        + "\"Class\":\"Account\"}";
    try {
      clientNotifier.sendClass(account);
      String result = listenForString();
      assertEquals(expected, result);
    } catch (IOException e) {
      fail("IOException, could not send string.");
    }
  }

  @Test
  public void listenForFileTest() {
    String path = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "services" + File.separator + "TestFile.txt";

    File file = new File(path);
    byte[] byteArray = new byte[(int) file.length()];
    DataInputStream dis;

    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      try {
        dis.readFully(byteArray, 0, byteArray.length);
      } catch (IOException e) {
        fail(e);
      }
    } catch (FileNotFoundException e) {
      fail(e);
    }

    try {
      dosToBeWrittenTooByTest.writeUTF(file.getName());
      dosToBeWrittenTooByTest.writeLong(byteArray.length);
      dosToBeWrittenTooByTest.write(byteArray, 0, byteArray.length);
      dosToBeWrittenTooByTest.flush();
    } catch (IOException e) {
      fail(e);
    }

    String savePath = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded" + File.separator
        + "textFiles" + File.separator;

    File expectedFile = new File(savePath + "TestFile.txt");
    try {
      File result = clientNotifier.listenForFile(savePath);
      assertEquals(expectedFile.getPath(), result.getPath());
    } catch (IOException e) {
      e.printStackTrace();
      fail(e);
    }

    path = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded" + File.separator
        + "textFiles" + File.separator + "TestFile.txt";

    file = new File(path);

    if (file.delete()) {
      log.info("Clean up successful.");
    } else {
      log.warn("Clean up failed.");
    }
  }

  @Test
  public void sendJsonTest() {
    Gson gson = new Gson();
    Account account = new Account("username", "password");
    JsonObject jsonObject = gson.fromJson(clientNotifier.packageClass(account), JsonObject.class);
    String expected = "{\"userID\":0,\"username\":\"username\",\"hashedpw\":\"password\","
        + "\"tutorStatus\":0,\"isRegister\":0,\"rating\":0.0,\"followedSubjects\":[],"
        + "\"Class\":\"Account\"}";
    try {
      clientNotifier.sendJson(jsonObject);
      String result = listenForString();
      assertEquals(expected, result);
    } catch (IOException e) {
      fail("IOException, could not send string.");
    }
  }

  @Test
  public void sendJsonArrayTest() {
    // TODO Complete Test
  }

  @Test
  public void sendStringTest() {
    String testString = "someString";
    String result;
    try {
      clientNotifier.sendString(testString);
      result = listenForString();
      assertEquals(testString, result);
    } catch (IOException e) {
      fail("IOException, could not send string.");
    }
  }

  @Test
  public void packageClassTest() {
    String path = "src" + File.separator + "test"
        + File.separator + "resources" + File.separator + "services" + File.separator
        + "TestFile.txt";

    File testObject = new File(path);
    String response;

    response = clientNotifier.packageClass(testObject);

    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
      assertEquals("File", jsonObject.get("Class").getAsString());
    } catch (JsonSyntaxException e) {
      fail("Did not return a json object.");
    }
  }

  @Test
  public void sendSubjectToHomeTest() {
    try {
      when(mySqlMock.getSubjects()).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(true, false);
      when(resultSetMock.getInt("subjectID")).thenReturn(1);
      when(resultSetMock.getString("subjectname")).thenReturn("TestSubject");
      when(mySqlMock.getSubjectCategory(1)).thenReturn("TestCategory");
      when(mySqlMock.isSubjectFollowed(1, 1)).thenReturn(true);

      clientNotifier.sendSubjects(mySqlMock, 0, null,
          1, "Home");
      Thread.sleep(100);
      String result = listenForString();

      String expected = "{\"id\":1,\"name\":\"TestSubject\",\"category\":\"TestCategory\","
          + "\"isFollowed\":true,\"Class\":\"SubjectHomeWindowResponse\"}";

      assertEquals(expected, result);
    } catch (SQLException | InterruptedException | IOException sqlException) {
      fail();
    }
  }

  @Test
  public void sendSubjectToSubscriptionsTest() {
    try {
      when(mySqlMock.getSubjects()).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(true, false);
      when(resultSetMock.getInt("subjectID")).thenReturn(1);
      when(resultSetMock.getString("subjectname")).thenReturn("TestSubject");
      when(mySqlMock.getSubjectCategory(1)).thenReturn("TestCategory");
      when(mySqlMock.isSubjectFollowed(1, 1)).thenReturn(true);

      clientNotifier.sendSubjects(mySqlMock, 0, null,
          1, "Subscriptions");
      Thread.sleep(100);
      String result = listenForString();

      String expected = "{\"id\":1,\"name\":\"TestSubject\",\"category\":\"TestCategory\","
          + "\"isFollowed\":true,\"Class\":\"SubjectSubscriptionsWindowResponse\"}";

      assertEquals(expected, result);
    } catch (SQLException | InterruptedException | IOException sqlException) {
      fail();
    }
  }

  @Test
  public void sendSubjectWithCategoryTest() {
    try {
      when(mySqlMock.getSubjectID("ReferenceSubject")).thenReturn(2);
      when(mySqlMock.getSubjectCategory(2)).thenReturn("Science");
      when(mySqlMock.getCategoryID("Science")).thenReturn(1);
      when(mySqlMock.getSubjects(1)).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(true, false);
      when(resultSetMock.getInt("subjectID")).thenReturn(1);
      when(resultSetMock.getString("subjectname")).thenReturn("TestSubject");
      when(mySqlMock.getSubjectCategory(1)).thenReturn("Science");
      when(mySqlMock.isSubjectFollowed(1, 1)).thenReturn(true);

      clientNotifier.sendSubjects(mySqlMock, 0, "ReferenceSubject",
          1, "Subscriptions");
      Thread.sleep(100);
      String result = listenForString();

      String expected = "{\"id\":1,\"name\":\"TestSubject\",\"category\":\"Science\","
          + "\"originalSubject\":\"ReferenceSubject\",\"isFollowed\":true,"
          + "\"Class\":\"SubjectSubscriptionsWindowResponse\"}";

      assertEquals(expected, result);
    } catch (SQLException | InterruptedException | IOException sqlException) {
      fail();
    }
  }

  @Test
  public void sendTopTutorsTest() {
    try {
      when(mySqlMock.getTutorsDescendingByAvgRating()).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(true, false);
      when(resultSetMock.getInt("tutorID")).thenReturn(5);
      when(resultSetMock.getFloat("rating")).thenReturn((float) 3.5);
      when(mySqlMock.getUsername(5)).thenReturn("TutorName");
      when(mySqlMock.isTutorFollowed(5, 1)).thenReturn(true);

      clientNotifier.sendTopTutors(mySqlMock, 0, 1);
      Thread.sleep(100);
      String result = listenForString();

      String expected = "{\"tutorName\":\"TutorName\",\"tutorID\":5,\"rating\":3.5,"
          + "\"isFollowed\":true,\"Class\":\"TopTutorHomeWindowResponse\"}";

      assertEquals(expected, result);
    } catch (SQLException | InterruptedException | IOException sqlException) {
      fail();
    }
  }

  @Test
  public void sendLiveTutorsTest() {
    try {
      when(mySqlMock.getFollowedTutors(1)).thenReturn(resultSetMock);
      when(resultSetMock.next()).thenReturn(true, false);
      when(resultSetMock.getInt("tutorID")).thenReturn(5);
      when(mySqlMock.getTutorsRating(5, 1)).thenReturn(3);
      when(mySqlMock.isTutorLive(5)).thenReturn(true);

      clientNotifier.sendLiveTutors(mySqlMock, 1);
      Thread.sleep(100);
      String result = listenForString();

      String expected = "{\"tutorID\":5,\"rating\":3.0,\"isLive\":true,"
          + "\"Class\":\"LiveTutorHomeWindowUpdate\"}";

      assertEquals(expected, result);
    } catch (SQLException | InterruptedException | IOException sqlException) {
      fail();
    }
  }

  /**
   * This is used to to listen for the MainConnections response
   * to test data. This confirms that the correct and complete
   * strings are being sent by the MainConnection.
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