import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
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

  private String sessionID;
  private String tutorID;
  private boolean tutorOnlyAccess;
  private ConcurrentHashMap<Integer, ClientHandler> activeClients;
  private ArrayList<Integer> sessionUsers;
  private ArrayList<JsonObject> jsonQueue;
  private ArrayList<JsonObject> sessionHistory;
  private boolean running = true;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardHandler");

  /**
   * Main class constructor.
   *
   * @param sessionID ID of the stream session.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public WhiteboardHandler(String sessionID, String tutorID, int token,
      ConcurrentHashMap<Integer, ClientHandler> activeClients, boolean tutorOnlyAccess) {

    setDaemon(true);
    setName("WhiteboardHandler-" + token);

    // Assign unique session ID and tutor ID to new whiteboard handler.
    this.sessionID = sessionID;
    this.tutorID = tutorID;
    this.activeClients = activeClients;
    this.tutorOnlyAccess = tutorOnlyAccess;

    // Add tutor to session users.
    this.sessionUsers = new ArrayList<Integer>();
    this.jsonQueue = new ArrayList<JsonObject>();
    this.sessionHistory = new ArrayList<JsonObject>();
    addUser(token);
  }

  /**
   * Transmit the incoming session package queue.
   */
  @Override
  public void run() {

    while (running) {
      synchronized (jsonQueue) {
        if (!jsonQueue.isEmpty()) {
          log.info("Length - " + jsonQueue.size());
          JsonObject currentPackage = jsonQueue.remove(0);
          log.info("Request: " + currentPackage.toString());
          String userID = currentPackage.get("userID").getAsString();

          // Update access control.
          String state = currentPackage.get("mouseState").getAsString();
          if (state.equals("access")) {
            String access = currentPackage.get("canvasTool").getAsString();
            this.tutorOnlyAccess = Boolean.valueOf(access);

          // Allow tutor to update whiteboard regardless of access control.
          // Ignore all null state packages.
          } else if (this.tutorID.equals(userID) || !tutorOnlyAccess) {
            // Store package in session history.
            sessionHistory.add(currentPackage);
            // Update for all users.
            for (Integer user : sessionUsers) {
              log.info("User " + user);
              activeClients.get(user).getNotifier().sendJson(currentPackage);
            }
          }
        }
      }
    }
  }

  public synchronized void addToQueue(JsonObject request) {
    log.info("Request - " + request.toString());
    jsonQueue.add(request);
  }

  public synchronized void addUser(Integer userToken) {
    this.sessionUsers.add(userToken);

    if (!this.sessionHistory.isEmpty()) {
      log.info(sessionHistory.toString());
      this.activeClients.get(userToken).getNotifier().sendJsonArray(this.sessionHistory);
    } else {
      log.info("No Session History.");
    }
  }

  public void removeUser(Integer userToken) {
    sessionUsers.remove((Object) userToken);
  }

  /* Setters and Getters */

  public ArrayList<Integer> getSessionUsers() {
    return this.sessionUsers;
  }

  public String getSessionID() {
    return sessionID;
  }

  public ArrayList<JsonObject> getSessionHistory() {
    log.info(sessionHistory.toString());
    return sessionHistory;
  }

  public void exit() {
    this.running = false;
  }
}
