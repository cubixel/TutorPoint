import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description.
 *
 * @author James Gardner
 */
public class Session {

  private int sessionID;
  private boolean isLive;
  private ConcurrentHashMap<Integer, ClientHandler> sessionUsers;
  private ClientHandler thisHandler;

  private PresentationHandler presentationHandler;

  private static final Logger log = LoggerFactory.getLogger("Session");

  /**
   * Creates a Session.
   */
  public Session(int sessionID, ClientHandler thisHandler) {
    this.sessionID = sessionID;
    this.isLive = false;
    this.thisHandler = thisHandler;
    this.sessionUsers = new ConcurrentHashMap<Integer, ClientHandler>();
  }

  /**
   * Sets up all module handlers for the clients stream screen.
   *
   * @return {@code true} if the session is ready and {@code false}
   *         if not
   */
  public boolean setUp() {
    // TODO Any setup required and then calls to all module handlers setup
    presentationHandler = new PresentationHandler(this);
    presentationHandler.start();
    return true;
  }

  /**
   * Perform Cleanup before exit.
   */
  public boolean cleanUp() {
    log.info("Performing cleanup...");
    // Kick all users
    sessionUsers.forEach((id, handler) -> {
      stopWatching(id, handler);
    });
    // Exit all session handlers
    presentationHandler.exit();
    // Any Additional Cleanup
    return true;
  }

  /**
   * .
   * @param userId .
   * @return
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

  public void stopWatching(int userID, ClientHandler handler) {
    log.info("Removing user " + userID + " from session");
    sessionUsers.remove(userID, handler);
  }

  public ClientHandler getThisHandler() {
    return thisHandler;
  }

  public PresentationHandler getPresentationHandler() {
    return presentationHandler;
  }
}
