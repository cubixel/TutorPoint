import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.gson.JsonObject;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import services.ClientNotifier;

/**
 * This is testing the TextChatHandler class. It updates
 * the package queue with a new JsonObject packages to test
 * this is sent to the host and other users.
 *
 * @author James Gardner
 *
 * @see TextChatHandler
 */
public class TextChatHandlerTest {

  @Mock
  private ClientHandler clientHandlerMock;

  @Mock
  private Session sessionMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> sessionUsersMock;

  @Mock
  private ClientNotifier clientNotifierMock;

  private TextChatHandler textChatHandler;

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    textChatHandler = new TextChatHandler(sessionMock);
  }

  @Test
  public void updatingPackageList() {
    textChatHandler.start();

    when(sessionMock.getThisHandler()).thenReturn(clientHandlerMock);
    when(sessionMock.getSessionUsers()).thenReturn(sessionUsersMock);
    when(clientHandlerMock.getNotifier()).thenReturn(clientNotifierMock);

    JsonObject sessionPackage = new JsonObject();
    sessionPackage.addProperty("testpackage", "test");

    textChatHandler.addToQueue(sessionPackage);

    long start = System.currentTimeMillis();
    long end = start + 2000;
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertEquals(1, textChatHandler.getSessionHistory().size());
    verify(clientNotifierMock, times(1)).sendJson(sessionPackage);
  }
}