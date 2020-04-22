import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author James Gardner
 * @author
 * @author
 */
public class Session {

  private int sessionID;
  private boolean isLive;
  private HashMap<Integer, ClientHandler> sessionUsers;

  private static final Logger log = LoggerFactory.getLogger("Session");

  public Session(int sessionID) {
    this.sessionID = sessionID;
    this.isLive = false;
  }

  /**
   * Sets up all module handlers for the clients stream screen.
   *
   * @return {@code true} if the session is ready and {@code false}
   *         if not
   */
  public boolean setUp() {
    // TODO Any setup required and then calls to all module handlers setup
    return true;
  }

  public boolean isLive() {
    return isLive;
  }

  public void setLive(boolean live) {
    isLive = live;
  }

  public HashMap<Integer, ClientHandler> getSessionUsers() {
    return sessionUsers;
  }
}
