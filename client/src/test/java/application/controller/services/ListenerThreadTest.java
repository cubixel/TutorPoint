package application.controller.services;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerThreadTest {

  private ListenerThread listenerThread;
  private DataInputStream disForTestToReceiveResponse;
  private DataOutputStream dosToBeWrittenTooByListenerThread;
  private DataInputStream disReceivingDataFromTest;
  private DataOutputStream dosToBeWrittenTooByTest;
  private int token = 1;

  private static final Logger log = LoggerFactory.getLogger("ListenerThreadTest");

  /**
   * Used to create the DataInput/OutputStreams used
   * to communicate between the test and the ClientHandler.
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

    try {
      listenerThread = new ListenerThread(disReceivingDataFromTest,
          dosToBeWrittenTooByListenerThread, token);
      listenerThread.start();
      disForTestToReceiveResponse.readInt();
      dosToBeWrittenTooByTest.writeInt(1);
    } catch (IOException e) {
      log.error("Failed to setup ListenerThread");
      fail(e);
    }

    log.info("Setup complete, running test");
  }

  @Test
  public void test() {
    log.info("this is a test");
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
