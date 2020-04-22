import java.util.HashMap;
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
  private HashMap<Integer, ClientHandler> sessionUsers;
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
  }

  /**
   * Sets up all module handlers for the clients stream screen.
   *
   * @return {@code true} if the session is ready and {@code false}
   *         if not
   */
  public boolean setUp() {
    // TODO Any setup required and then calls to all module handlers setup
    presentationHandler = new PresentationHandler(this, sessionID);
    presentationHandler.start();
    return true;
  }

  /**
   * Perform Cleanup before exit.
   */
  public boolean cleanUp() {
    // TODO Calls to all module handlers cleanup then does any extra cleanup
    presentationHandler.exit();
    return true;
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

  public HashMap<Integer, ClientHandler> getSessionUsers() {
    return sessionUsers;
  }

  public ClientHandler getThisHandler() {
    return thisHandler;
  }

  public PresentationHandler getPresentationHandler() {
    return presentationHandler;
  }
}
