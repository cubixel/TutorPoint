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
          log.debug(String.valueOf(userID));
          // Update access control.
          String state = currentPackage.get("mouseState").getAsString();
          log.debug(state);

          // Allow tutor to update whiteboard regardless of access control.
          // Ignore all null state packages.
          log.info("User ID came from: " + userID + "; comparing to: " + session.getSessionID());
          if (session.getSessionID() == userID) {
            log.info("Packet came from host");
          } else {
            log.info("Packet came from not host");
          }

          if (state.equals("access")) {
            String access = currentPackage.get("canvasTool").getAsString();
            this.tutorOnlyAccess = Boolean.parseBoolean(access);
          } else if ((session.getSessionID() == userID) || (!tutorOnlyAccess)) {
            // Store package in session history.
            sessionHistory.add(currentPackage);
          
            // Update for all users.
            session.getSessionUsers().forEach((id, handler) -> {
              handler.getNotifier().sendJson(currentPackage);
              log.info("Sent to id " + id);
            });


            //TODO if userId not session id and tutoraccess false send to host
            if ((!tutorOnlyAccess) && (session.getSessionID() != userID)) {
              session.getThisHandler().getNotifier().sendJson(currentPackage);
            }

          } else {
            log.debug(String.valueOf(tutorOnlyAccess));
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