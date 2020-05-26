import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;

/**
 * This is the test class for the Session class.
 *
 * @author James Gardner
 *
 * @see Session
 * @see PresentationHandler
 * @see TextChatHandler
 * @see WhiteboardHandler
 */
public class SessionTest {

  private Session session;

  private static final Logger log = LoggerFactory.getLogger("SessionTest");

  @Mock
  private MainServer mainServerMock;

  @Mock
  private ClientHandler clientHandlerMock;

  @Mock
  private ClientHandler otherUserClientHandlerMock;

  @Mock
  private MySql mySqlMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> sessionUsersMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> loggedInClientsMock;

  @Mock
  private PresentationHandlerFactory presentationHandlerFactoryMock;

  @Mock
  private TextChatHandlerFactory textChatHandlerFactoryMock;

  @Mock
  private WhiteboardHandlerFactory whiteboardHandlerFactoryMock;

  @Mock
  private PresentationHandler presentationHandlerMock;

  @Mock
  private TextChatHandler textChatHandlerMock;

  @Mock
  private WhiteboardHandler whiteboardHandlerMock;

  /**
   * Used to instantiate mocks and create a session to test on
   * prior to each test.
   */
  @BeforeEach
  public void setUp() {
    log.info("Initialising setup...");
    MockitoAnnotations.initMocks(this);

    int sessionID = 1;

    session = new Session(sessionID, clientHandlerMock, sessionUsersMock,
        presentationHandlerFactoryMock, textChatHandlerFactoryMock, whiteboardHandlerFactoryMock);

    when(presentationHandlerFactoryMock.createPresentationHandler(session))
        .thenReturn(presentationHandlerMock);
    when(textChatHandlerFactoryMock.createTextChatHandler(session))
        .thenReturn(textChatHandlerMock);
    when(whiteboardHandlerFactoryMock.createWhiteboardHandler(session, true))
        .thenReturn(whiteboardHandlerMock);

    assertTrue(session.setUp());
  }

  @Test
  public void kickAllTest() {
    sessionUsersMock.put(1, clientHandlerMock);
    session.kickAll();
    verify(sessionUsersMock, times(1)).forEach(any());
  }

  @Test
  public void cleanUpTest() {
    when(clientHandlerMock.getSqlConnection()).thenReturn(mySqlMock);
    when(clientHandlerMock.getUserID()).thenReturn(2);

    session.cleanUp();

    verify(presentationHandlerMock, times(1)).exit();
    verify(whiteboardHandlerMock, times(1)).exit();
  }

  @Test
  public void requestJoinTest() {
    int userID = 1;

    when(sessionUsersMock.containsKey(userID)).thenReturn(true);

    assertFalse(session.requestJoin(userID));

    when(sessionUsersMock.containsKey(userID)).thenReturn(false);
    when(clientHandlerMock.getMainServer()).thenReturn(mainServerMock);
    when(mainServerMock.getLoggedInClients()).thenReturn(loggedInClientsMock);
    when(loggedInClientsMock.get(userID)).thenReturn(otherUserClientHandlerMock);

    assertTrue(session.requestJoin(userID));

    verify(sessionUsersMock, times(1)).put(userID, otherUserClientHandlerMock);

    verify(presentationHandlerMock, times(1))
        .sendXmlToClientListener(otherUserClientHandlerMock);
  }

  @Test
  public void stopWatchingTest() {
    int userID = 1;
    session.stopWatching(userID, otherUserClientHandlerMock);
    verify(sessionUsersMock, times(1))
        .remove(userID, otherUserClientHandlerMock);
  }
}