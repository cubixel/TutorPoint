import com.google.gson.JsonObject;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the whiteboard session packages
 * received by the client handler.
 *
 * @author Oliver Still
 * @author Che McKirgan
 */
public class WhiteboardHandler extends Thread {

  private final Session session;
  private boolean tutorOnlyAccess;
  private final ArrayList<JsonObject> jsonQueue;
  private final ArrayList<JsonObject> sessionHistory;
  private boolean running = true;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardHandler");

  /**
   * Main class constructor.
   *
   */
  public WhiteboardHandler(Session session, boolean tutorOnlyAccess) {

    setDaemon(true);
    setName("WhiteboardHandler-" + session.getSessionID());

    // Assign unique session ID and tutor ID to new whiteboard handler.
    this.session = session;
    this.tutorOnlyAccess = tutorOnlyAccess;

    // Add tutor to session users.
    this.jsonQueue = new ArrayList<>();
    this.sessionHistory = new ArrayList<>();
  }

  /**
   * Transmit the incoming session package queue.
   */
  @Override
  public void run() {

    while (running) {
      synchronized (jsonQueue) {
        if (!jsonQueue.isEmpty()) {
          JsonObject currentPackage = jsonQueue.remove(0);
          int userID = currentPackage.get("userID").getAsInt();

          // Update access control.
          String state = currentPackage.get("mouseState").getAsString();
          if (state.equals("access")) {
            String access = currentPackage.get("canvasTool").getAsString();
            this.tutorOnlyAccess = Boolean.parseBoolean(access);

          // Allow tutor to update whiteboard regardless of access control.
          // Ignore all null state packages.
          } else if (session.getSessionID() == userID || !tutorOnlyAccess) {
            // Store package in session history.
            sessionHistory.add(currentPackage);
            // Update for all users.
            session.getSessionUsers().forEach((id, handler) ->
                handler.getNotifier().sendJson(currentPackage));

          }
        }
      }
    }
  }

  public synchronized void addToQueue(JsonObject request) {
    jsonQueue.add(request);
  }

  /* Setters and Getters */

  public ArrayList<JsonObject> getSessionHistory() {
    log.info(sessionHistory.toString());
    return sessionHistory;
  }

  public boolean isTutorOnlyAccess() {
    return tutorOnlyAccess;
  }

  public void exit() {
    this.running = false;
  }
}