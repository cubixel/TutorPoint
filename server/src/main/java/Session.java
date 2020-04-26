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
    this.sessionUsers = new HashMap<Integer, ClientHandler>();
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
    log.info("Performing cleanup...");
    presentationHandler.exit();
    return true;
  }

  public boolean requestJoin(int userId) {
    if (sessionUsers.containsKey(userId)) {
      return false;
    } else {
      ClientHandler newUserHandler = thisHandler.getMainServer().getLoggedInClients().get(userId);
      sessionUsers.put(userId, newUserHandler);
      //TODO send setup data for whiteboard and text chat here
      sendPresentationData(newUserHandler);
      log.info("Sent presentation to new user, ID: " + userId);
      return true;
    }
  }

  public void sendPresentationData(ClientHandler recipient) {
    recipient.getNotifier().sendString("SendingPresentation");
    presentationHandler.sendXml(recipient.getNotifier().getDataOutputStream());
    recipient.getNotifier().sendString(String.valueOf(presentationHandler.getSlideNum()));
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
