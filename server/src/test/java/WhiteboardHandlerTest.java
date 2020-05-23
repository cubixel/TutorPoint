import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.gson.JsonObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class WhiteboardHandlerTest {

  private WhiteboardHandler whiteboardHandler;
  private Session session;

  @Mock
  private Socket socket;

  @Mock
  private DataInputStream dis;

  @Mock
  private DataOutputStream dos;

  @Mock
  private ClientHandler parent;



  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    Session session = new Session(0, parent);

    session.requestJoin(0);
    whiteboardHandler = new WhiteboardHandler(session, true);
    whiteboardHandler.start();
  }

  @Test
  public void testSessionHistory() {

    JsonObject session = new JsonObject();
    session.addProperty("userID", 0);
    session.addProperty("mouseState", "active");

    whiteboardHandler.addToQueue(session);

    assertNotEquals(whiteboardHandler.getSessionHistory().size(), 0);
  }
}