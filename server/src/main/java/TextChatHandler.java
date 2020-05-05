import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION: This class is used by the server to manage/handle clients for text chat
 * services. I.e. to send and receive messages through the server.
 *
 * @author Oli Clarke
 */

public class TextChatHandler extends Thread {

  private ArrayList<JsonObject> jsonQueue;                 // Queue for message updates.
  private ArrayList<JsonObject> sessionHistory;            // Session History
  private Session session;                                 // Session for handler.
  private boolean running = true;
  private static final Logger log = LoggerFactory.getLogger("TextChatHandler");

  /**
   * Main class constructor.
   */
  public TextChatHandler(Session session) {
    setDaemon(true);
    setName("TextChatHandler-" + session.getSessionID()); // SessionID text chat handler.
    this.session = session;

    // Add tutor to session users.
    this.jsonQueue = new ArrayList<JsonObject>();
    this.sessionHistory = new ArrayList<JsonObject>();
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
          // Store package in session history.
          sessionHistory.add(currentPackage);
          // Send message to tutor
          session.getThisHandler().getNotifier().sendJson(currentPackage);
          // Update for all users.
          session.getSessionUsers().forEach((id, handler) -> {
            handler.getNotifier().sendJson(currentPackage);
          });
        }
      }
    }
  }

  /**
   * Remove user from server.
   */

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

}