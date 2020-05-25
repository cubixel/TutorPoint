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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    listenerThread.start();

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
}