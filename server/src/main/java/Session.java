import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Session class contains all handlers and users
 * involved in a session. It manages users joining
 * and leaving the session and ensures all users are
 * update with changes to either the TextChat,
 * Presentations, Whiteboard and Webcam modules.
 *
 * @author James Gardner
 * @author Daniel Bishop
 * @author Eric Walker
 * @author Oliver Clarke
 * @author Oliver Still
 */
public class Session {

  private final int sessionID;
  private boolean isLive;
  private final ConcurrentHashMap<Integer, ClientHandler> sessionUsers;
  private final ClientHandler thisHandler;

  private final PresentationHandlerFactory presentationHandlerFactory;
  private final TextChatHandlerFactory textChatHandlerFactory;
  private final WhiteboardHandlerFactory whiteboardHandlerFactory;

  private PresentationHandler presentationHandler;
  private TextChatHandler textChatHandler;
  private WhiteboardHandler whiteboardHandler;

  // TODO private WebcamHandler webcamHandler;

  private static final Logger log = LoggerFactory.getLogger("Session");

  /**
   * Creates a Session.
   */
  public Session(int sessionID, ClientHandler thisHandler) {
    this.sessionID = sessionID;
    this.isLive = false;
    this.thisHandler = thisHandler;
    this.sessionUsers = new ConcurrentHashMap<>();
    presentationHandlerFactory = new PresentationHandlerFactory();
    textChatHandlerFactory = new TextChatHandlerFactory();
    whiteboardHandlerFactory = new WhiteboardHandlerFactory();
  }

  /**
   * Creates a Session for testing.
   */
  public Session(int sessionID, ClientHandler thisHandler,
      ConcurrentHashMap<Integer, ClientHandler> sessionUsers,
      PresentationHandlerFactory presentationHandlerFactory,
      TextChatHandlerFactory textChatHandlerFactory,
      WhiteboardHandlerFactory whiteboardHandlerFactory) {
    this.sessionID = sessionID;
    this.isLive = false;
    this.thisHandler = thisHandler;
    this.sessionUsers = sessionUsers;
    this.presentationHandlerFactory = presentationHandlerFactory;
    this.textChatHandlerFactory = textChatHandlerFactory;
    this.whiteboardHandlerFactory = whiteboardHandlerFactory;
  }

  /**
   * Sets up all module handlers for the clients stream screen.
   *
   * @return {@code true} if the session is ready and {@code false} if not
   */
  public boolean setUp() {
    // TODO Any setup required and then calls to all module handlers setup

    textChatHandler = textChatHandlerFactory.createTextChatHandler(this);
    textChatHandler.start();
    presentationHandler = presentationHandlerFactory.createPresentationHandler(this);
    presentationHandler.start();
    whiteboardHandler = whiteboardHandlerFactory.createWhiteboardHandler(this, false);
    whiteboardHandler.start();
    return true;
  }

  public void kickAll() {
    // Kick all users
    sessionUsers.forEach((id, handler) -> {
      stopWatching(id, handler);
      handler.getNotifier().sendString("StreamKicked");
    });
  }

  /**
   * Perform Cleanup before exit.
   */
  public void cleanUp() {
    log.info("Performing cleanup...");
    kickAll();
    // Exit all session handlers
    presentationHandler.exit();
    whiteboardHandler.exit();
    // Remove session from database
    try {
      thisHandler.getSqlConnection().endLiveSession(sessionID, thisHandler.getUserID());
    } catch (SQLException e) {
      log.error("Failed to clean up session on database", e);
    }
    // Any Additional Cleanup
  }

  /**
   * Joins a user to this session. Finds the users ClientHandler based off
   * of the userID provided and puts them both in the sessionUsers hash map.
   *
   * @param userId
   *        The unique integer identifying the user requesting to join the session
   *
   * @return {@code true} if the userID wasn't already in the session and {@code false}
   *         if it was
   */
  public boolean requestJoin(int userId) {
    if (sessionUsers.containsKey(userId)) {
      return false;
    } else {
      ClientHandler newUserHandler = thisHandler.getMainServer().getLoggedInClients().get(userId);
      sessionUsers.put(userId, newUserHandler);
      //TODO send setup data for whiteboard and text chat here
      
      presentationHandler.sendXmlToClientListener(newUserHandler);
      log.info("Sent presentation to new user, ID: " + userId);
      return true;
    }
  }


  public boolean isLive() {
    return isLive;
  }

  public void setLive(boolean live) {
    isLive = live;
  }

  public int getSessionID() {
    return sessionID;
  }

  public ConcurrentHashMap<Integer, ClientHandler> getSessionUsers() {
    return sessionUsers;
  }

  /**
   * Removes the userID and its associated ClientHandler
   * from the list of current sessionUsers.
   *
   * @param userID
   *        The unique integer identifying that user
   *
   * @param handler
   *        The ClientHandler associated with that user
   */
  public void stopWatching(int userID, ClientHandler handler) {
    log.info("Removing user " + userID + " from session");
    sessionUsers.remove(userID, handler);
    //TODO tell handler they have stopped watching
  }

  public ClientHandler getThisHandler() {
    return thisHandler;
  }

  public PresentationHandler getPresentationHandler() {
    return presentationHandler;
  }

  public WhiteboardHandler getWhiteboardHandler() {
    return whiteboardHandler;
  }

  public TextChatHandler getTextChatHandler() {
    return textChatHandler;
  }
}
