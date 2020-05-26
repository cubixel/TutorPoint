package application.controller.services;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import application.controller.HomeWindowController;
import application.controller.PresentationWindowController;
import application.controller.SubscriptionsWindowController;
import application.controller.TutorWindowController;
import com.google.gson.JsonObject;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the test class for the ListenerThread it
 * tests all possible inputs the ListenerThread can receive.
 *
 * @author James Gardner
 */
public class ListenerThreadTest {

  @Mock
  private PresentationWindowController presentationWindowControllerMock;

  @Mock
  private HomeWindowController homeWindowControllerMock;

  @Mock
  private TutorWindowController tutorWindowControllerMock;

  @Mock
  private SubscriptionsWindowController subscriptionsWindowControllerMock;

  @Mock
  private WhiteboardService whiteboardServiceMock;

  @Mock
  private TextChatService textChatServiceMock;

  private ListenerThread listenerThread;
  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByListenerThread;
  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;

  private static final Logger log = LoggerFactory.getLogger("ListenerThreadTest");

  /**
   * This method starts the JavaFX runtime. The specified Runnable will then be
   * called on the JavaFX Application Thread.
   */
  @BeforeAll
  public static void setUpToolkit() {
    Platform.startup(() -> log.info("Toolkit initialized ..."));
  }

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
      dosToBeWrittenTooByListenerThread = new DataOutputStream(new PipedOutputStream(pipeInputTwo));
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
   * METHOD DESCRIPTION.
   *
   * @throws IOException DESCRIPTION
   */
  @AfterEach
  public void cleanUp() throws IOException {
    disForTestToReceiveResponse.close();
    dosToBeWrittenTooByListenerThread.close();
    disReceivingDataFromTest.close();
    dosToBeWrittenTooByTest.close();
  }

  @Test
  public void whiteboardSessionTest() {
    listenerThread.start();

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "WhiteboardSession");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(whiteboardServiceMock, times(1)).updateWhiteboardSession(any());
  }

  @Test
  public void subjectHomeWindowResponseTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "SubjectHomeWindowResponse");
    jsonObject.addProperty("id", 1);
    jsonObject.addProperty("name", "TestName");
    jsonObject.addProperty("category", "TestCategory");
    jsonObject.addProperty("isFollowed", "true");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(homeWindowControllerMock, times(1)).addSubjectLink(any());
  }

  @Test
  public void subjectSubscriptionsWindowResponseTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "SubjectSubscriptionsWindowResponse");
    jsonObject.addProperty("id", 1);
    jsonObject.addProperty("name", "TestName");
    jsonObject.addProperty("category", "TestCategory");
    jsonObject.addProperty("isFollowed", "true");
    jsonObject.addProperty("originalSubject", "OriginalSubject");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(subscriptionsWindowControllerMock, times(1)).addSubjectLink(any(), any());
  }

  @Test
  public void topTutorHomeWindowResponseTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "TopTutorHomeWindowResponse");
    jsonObject.addProperty("tutorName", "TestName");
    jsonObject.addProperty("tutorID", 1);
    jsonObject.addProperty("rating", 3.5);
    jsonObject.addProperty("isFollowed", "true");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(homeWindowControllerMock, times(1)).addTutorLink(any());
  }

  @Test
  public void liveTutorHomeWindowUpdateTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "LiveTutorHomeWindowUpdate");
    jsonObject.addProperty("tutorName", "TestName");
    jsonObject.addProperty("tutorID", 1);
    jsonObject.addProperty("rating", 3.5);
    jsonObject.addProperty("isFollowed", "true");
    jsonObject.addProperty("isLive", "true");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(homeWindowControllerMock, times(1)).addLiveTutorLink(any());
  }

  @Test
  public void presentationChangeSlideRequestTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "PresentationChangeSlideRequest");
    jsonObject.addProperty("slideNum", 1);

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(presentationWindowControllerMock, times(1)).setSlideNum(1);
  }

  @Test
  public void textChatSessionTest() {
    Platform.runLater(() -> listenerThread.start());

    /* Building JsonObject for simulation of input */
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("Class", "TextChatSession");

    try {
      dosToBeWrittenTooByTest.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Could not write to DataOutputStream", e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    verify(textChatServiceMock, times(1)).updateTextChatSession(any());
  }

  @Test
  public void sendingPresentationTest() {
    Platform.runLater(() -> listenerThread.start());

    String path = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "services" + File.separator + "TestXML.xml";

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
      dosToBeWrittenTooByTest.writeUTF("SendingPresentation");
      dosToBeWrittenTooByTest.writeUTF(file.getName());
      dosToBeWrittenTooByTest.writeLong(byteArray.length);
      dosToBeWrittenTooByTest.write(byteArray, 0, byteArray.length);
      dosToBeWrittenTooByTest.flush();
      dosToBeWrittenTooByTest.writeUTF("1");
    } catch (IOException e) {
      fail(e);
    }

    long start = System.currentTimeMillis();
    long end = start + 2000;

    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    path = "client" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "application" + File.separator
        + "media" + File.separator + "downloads" + File.separator + "currentPresentation.xml";

    try {
      file = new File(path);

      if (file.delete()) {
        log.info("Clean up successful.");
      } else {
        log.warn("Clean up failed.");
      }
    } catch (NullPointerException nullPointerException) {
      fail(nullPointerException);
    }
  }
}