package application.controller.services;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the Heartbeat class, ensures that the
 * correct number of calls to MainConnection are made
 * during a set time.
 *
 * @author James Gardner
 */
public class HeartbeatTest {

  private Heartbeat heartbeat;
  private static final Logger log = LoggerFactory.getLogger("HeartbeatTest");

  @Mock
  private MainConnection mainConnectionMock;

  /**
   * Setups up Heartbeat instance to test on and initialises
   * Mockito objects.
   */
  @BeforeEach
  public void setUp() {
    log.info("Initialising setup...");
    MockitoAnnotations.initMocks(this);
    heartbeat = new Heartbeat(mainConnectionMock);
    log.info("Setup complete, running test");
  }

  @Test
  public void pingTimeTest() {
    heartbeat.start();
    try {
      Thread.sleep(9500);
    } catch (InterruptedException e) {
      log.error("Could not sleep thread", e);
      fail();
    }
    try {
      verify(mainConnectionMock, times(5)).sendString("Heartbeat");
    } catch (IOException e) {
      log.error("Could not access sendString method", e);
      fail();
    }
  }
}