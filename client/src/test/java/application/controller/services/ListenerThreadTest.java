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
import javafx.application.Platform;
import org.mockito.Mock;
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
  protected PresentationWindowController presentationWindowControllerMock;

  @Mock
  protected HomeWindowController homeWindowControllerMock;

  @Mock
  protected TutorWindowController tutorWindowControllerMock;

  @Mock
  protected SubscriptionsWindowController subscriptionsWindowControllerMock;

  @Mock
  protected WhiteboardService whiteboardServiceMock;

  @Mock
  protected TextChatService textChatServiceMock;

  protected ListenerThread listenerThread;
  protected DataInputStream disForTestToReceiveResponse;
  protected DataOutputStream dosToBeWrittenTooByListenerThread;
  protected DataInputStream disReceivingDataFromTest;
  protected DataOutputStream dosToBeWrittenTooByTest;

  protected static final Logger log = LoggerFactory.getLogger("ListenerThreadTest");

  /**
   * This is the whiteboardSessionTest.
   */
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

  /**
   * This is the subjectHomeWindowResponseTest.
   */
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

  /**
   * This is the subjectSubscriptionsWindowResponseTest.
   */
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

  /**
   * This is the topTutorHomeWindowResponseTest.
   */
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

  /**
   * This is the liveTutorHomeWindowUpdateTest.
   */
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

  /**
   * This is the presentationChangeSlideRequestTest.
   */
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

  /**
   * This is the textChatSessionTest.
   */
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

  /**
   * This is the sendingPresentationTest.
   */
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