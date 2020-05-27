package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.Account;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the test class for the MainConnection. It tests
 * all methods within the MainConnection class to ensure any changes
 * made to during development do not affect function of the MainConnection.

 * @author James Gardner
 * @see MainConnection
 */
public class MainConnectionTest {

  private  MainConnection mainConnection;
  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByMainConnection;
  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;

  private static final Logger log = LoggerFactory.getLogger("MainConnectionTest");

  @Mock
  private Heartbeat heartbeatMock;

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

      mainConnection = new MainConnection(disReceivingDataFromTest,
          dosToBeWrittenTooByMainConnection, heartbeatMock);
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
  public void stopHeartbeatTest() {
    mainConnection.stopHeartbeat();
    verify(heartbeatMock).stopHeartbeat();
  }

  @Test
  public void sendStringTest() {
    String testString = "someString";
    String result;
    try {
      mainConnection.sendString(testString);
      result = listenForString();
      assertEquals(testString, result);
    } catch (IOException e) {
      fail("IOException, could not send string.");
    }
  }

  @Test
  public void listenForStringTest() {
    String testString = "someString";
    String result = "failed";

    try {
      dosToBeWrittenTooByTest.writeUTF(testString);
    } catch (IOException e) {
      fail("Could not write to test DataOutputStream.");
    }

    try {
      result = mainConnection.listenForString();
    } catch (IOException e) {
      fail("Main connection could not read from its DataInputStream.");
    }

    assertEquals(testString, result);
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

    String expectedPath = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "application" + File.separator
        + "media" + File.separator + "downloads" + File.separator + "TestFile.txt";

    File expectedFile = new File(expectedPath);
    try {
      File result = mainConnection.listenForFile();
      assertEquals(expectedFile.getPath(), result.getPath());
    } catch (IOException e) {
      e.printStackTrace();
      fail(e);
    }

    path = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "application" + File.separator
        + "media" + File.separator + "downloads" + File.separator + "TestFile.txt";

    file = new File(path);

    if (file.delete()) {
      log.info("Clean up successful.");
    } else {
      log.warn("Clean up failed.");
    }
  }

  @Test
  public void sendFileTest() {
    try {
      String path = "src" + File.separator + "test" + File.separator + "resources"
          + File.separator + "services" + File.separator + "TestFile.txt";

      File file = new File(path);
      mainConnection.sendFile(file);

      listenForFile();
    } catch (IOException e) {
      fail(e);
    }

    String path = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "services" + File.separator + "DownloadTestFile.txt";

    File file = new File(path);

    if (file.delete()) {
      log.info("Clean up successful.");
    } else {
      log.warn("Clean up failed.");
    }
  }

  @Test
  public void packageClassTest() {
    String path = "src" + File.separator + "test"
        + File.separator + "resources" + File.separator + "services" + File.separator
        + "TestFile.txt";

    File testObject = new File(path);
    String response;

    response = mainConnection.packageClass(testObject);

    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
      assertEquals("File", jsonObject.get("Class").getAsString());
    } catch (JsonSyntaxException e) {
      fail("Did not return a json object.");
    }
  }

  @Test
  public void listenForStringTimeoutTest() {
    String expected = "FAILED_BY_NETWORK";
    String result = "failed";

    try {
      result = mainConnection.listenForString();
    } catch (IOException e) {
      fail("Main connection could not read from its DataInputStream.");
    }

    assertEquals(expected, result);
  }

  @Test
  public void listenForAccountTest() {
    Account account = new Account(0, "username", "email@test.com",
        "password", 1, 0);
    account.addFollowedSubjects("subjectOne");
    account.addFollowedSubjects("subjectTwo");
    try {
      dosToBeWrittenTooByTest.writeUTF(mainConnection.packageClass(account));
      Account resultingAccount = mainConnection.listenForAccount();
      assertEquals(0, resultingAccount.getUserID());
      assertEquals("username", resultingAccount.getUsername());
      assertEquals("email@test.com", resultingAccount.getEmailAddress());
      assertEquals("password", resultingAccount.getHashedpw());
      assertEquals(1, resultingAccount.getTutorStatus());
      assertEquals("subjectOne", resultingAccount.getFollowedSubjects().get(0));
      assertEquals("subjectOne", resultingAccount.getFollowedSubjects().get(0));
      assertNull(resultingAccount.getProfilePicture());
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void claimAndReleaseTest() {
    assertTrue(mainConnection.claim());
    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      assertFalse(mainConnection.claim());
    }

    mainConnection.release();
    assertTrue(mainConnection.claim());
    mainConnection.release();
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

  /**
   * Listens for a file from the server. Initially the server will send a {@code String}
   * containing the name of the file, then a {@code long} with the file size. Then it
   * sends the file as a stream of bytes that are used to construct the file.
   *
   * @throws IOException
   *         Thrown if error reading from DataInputStream or creating File
   */
  public void listenForFile() throws IOException {
    int bytesRead;

    String fileName = disForTestToReceiveResponse.readUTF();
    long size = disForTestToReceiveResponse.readLong();
    log.info("Listening for file named '" + fileName + "' of size " + size);

    String path = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "services" + File.separator;

    OutputStream output = new FileOutputStream(path + "Download" + fileName);

    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = disForTestToReceiveResponse.read(buffer, 0,
        (int) Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    output.close();
  }
}