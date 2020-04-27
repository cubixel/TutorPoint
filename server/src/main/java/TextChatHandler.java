import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION: This class is used by the server to manage/handle clients for text chat
 * services. I.e. to send and receive messages through the server.
 *
 * @author Oli Clarke
 */

public class TextChatHandler extends Thread {

  private Integer sessionID;                               // TextChat ID for connecting user.
  private HashMap<Integer, ClientHandler> activeClients;   // Active clients.
  private ArrayList<Integer> sessionUsers;                 // Users in a session.
  private ArrayList<JsonObject> jsonQueue;                 // Queue for message updates.
  private ArrayList<JsonObject> sessionHistory;            // Session History
  private boolean running = true;
  private static final Logger log = LoggerFactory.getLogger("TextChatHandler");

  /**
   * Main class constructor.
   */
  public TextChatHandler(Integer sessionID, int token,
      HashMap<Integer, ClientHandler> activeClients) {
    setDaemon(true);
    setName("TextChatHandler-" + token);
    // Assign unique session ID and tutor ID to new text chat handler.
    this.sessionID = sessionID;
    this.activeClients = activeClients;

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

          // Store package in session history.
          sessionHistory.add(currentPackage);
          // Update for all users.
          for (Integer user : sessionUsers) {
            log.info("User :" + user);
            activeClients.get(user).getNotifier().sendJson(currentPackage);
          }
        }
      }
    }
  }

  /**
   * Remove user from server.
   */
  public void removeUser(Integer userToken) {
    sessionUsers.remove((Object) userToken);
  }

  public void exit() {
    this.running = false;
  }

  /**
   * GETTERS & SETTERS.
   **/

  public ArrayList<JsonObject> getSessionHistory() {
    log.info(sessionHistory.toString());
    return sessionHistory;
  }

  public void addToQueue(JsonObject request) {
    jsonQueue.add(request);
  }

  public void addUser(Integer userToken) {
    this.sessionUsers.add(userToken);
  }

  public ArrayList<Integer> getSessionUsers() {
    return this.sessionUsers;
  }

  public Integer getSessionID() {
    return sessionID;
  }
}