import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.gson.JsonObject;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import services.ClientNotifier;

/**
 * This is testing the WhiteboardHandler class. It updates
 * the package queue with new JsonObject packages with various
 * parameters to test the Whiteboard Handler is functioning
 * correctly.
 *
 * @author Oliver Still
 * @author James Gardner
 *
 * @see WhiteboardHandler
 */
public class WhiteboardHandlerTest {

  @Mock
  private ClientHandler clientHandlerMock;

  @Mock
  private Session sessionMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> sessionUsersMock;

  @Mock
  private ClientNotifier clientNotifierMock;

  private WhiteboardHandler whiteboardHandler;


  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    whiteboardHandler = new WhiteboardHandler(sessionMock, true);
  }

  @Test
  public void testTutorOnlyAccess() {
    whiteboardHandler.start();

    when(sessionMock.getSessionID()).thenReturn(0);

    JsonObject session = new JsonObject();
    session.addProperty("userID", 1);
    session.addProperty("mouseState", "access");
    session.addProperty("canvasTool", "true");

    whiteboardHandler.addToQueue(session);

    long start = System.currentTimeMillis();
    long end = start + 2000;
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertTrue(whiteboardHandler.isTutorOnlyAccess());
    assertEquals(0, whiteboardHandler.getSessionHistory().size());
  }

  @Test
  public void changeTutorOnlyAccess() {
    whiteboardHandler.start();

    assertTrue(whiteboardHandler.isTutorOnlyAccess());

    when(sessionMock.getSessionID()).thenReturn(0);

    JsonObject session = new JsonObject();
    session.addProperty("userID", 1);
    session.addProperty("mouseState", "access");
    session.addProperty("canvasTool", "false");

    whiteboardHandler.addToQueue(session);

    long start = System.currentTimeMillis();
    long end = start + 2000;
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertFalse(whiteboardHandler.isTutorOnlyAccess());
    assertEquals(0, whiteboardHandler.getSessionHistory().size());
  }

  @Test
  public void updatingPackageList() {
    whiteboardHandler.start();

    when(sessionMock.getSessionID()).thenReturn(1);
    when(sessionMock.getSessionUsers()).thenReturn(sessionUsersMock);
    when(clientHandlerMock.getNotifier()).thenReturn(clientNotifierMock);

    JsonObject session = new JsonObject();
    session.addProperty("userID", 1);
    session.addProperty("mouseState", "other");
    session.addProperty("canvasTool", "false");

    whiteboardHandler.addToQueue(session);

    long start = System.currentTimeMillis();
    long end = start + 2000;
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertEquals(1, whiteboardHandler.getSessionHistory().size());
  }
}