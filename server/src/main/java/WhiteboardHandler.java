import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardHandler extends Thread {

  private String sessionID;
  private String tutorID;
  private boolean tutorOnlyAccess;
  private HashMap<Integer, ClientHandler> activeClients;
  private ArrayList<Integer> sessionUsers;
  private ArrayList<JsonObject> jsonQueue;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardHandler");

  /**
   * Constructor for WhiteboardHandler.
   * @param sessionID ID of the stream session.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public WhiteboardHandler(String sessionID, String tutorID, int token,
      HashMap<Integer, ClientHandler> activeClients) {
    setDaemon(true);
    setName("WhiteboardHandler-" + token);
    // Assign unique session ID and tutor ID to new whiteboard handler.
    this.sessionID = sessionID;
    this.tutorID = tutorID;
    this.activeClients = activeClients;

    // Add tutor to session users.
    this.sessionUsers = new ArrayList<Integer>();
    this.jsonQueue = new ArrayList<JsonObject>();
    addUser(token);
  }


  /**
   * Run the requested action in the PresentationHandler Thread.
   *
   * @param request The name of the action to perform.
   */
  public void run(JsonObject request) {
    JsonObject currentRequest = request;

    do {
      log.info("Request: " + currentRequest.toString());
      String userID = currentRequest.get("userID").getAsString();

      // Allow tutor to update whiteboard regardless of access control.
      if (this.tutorID.equals(userID) || !tutorOnlyAccess) {
        //Update for all users
        for (Integer user : sessionUsers) {
          activeClients.get(user).getNotifier().sendJson(currentRequest);
        }
      }

      if (!jsonQueue.isEmpty()) {
        currentRequest = jsonQueue.remove(0);
      }
    } while (!jsonQueue.isEmpty());
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

  public String getSessionID() {
    return sessionID;
  }
}
