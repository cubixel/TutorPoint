import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextChatHandler extends Thread {

  private String sessionID;
  private String tutorID;
  private HashMap<Integer, ClientHandler> activeClients;
  private ArrayList<Integer> sessionUsers;
  private ArrayList<JsonObject> jsonQueue;
  private static final Logger log = LoggerFactory.getLogger("TextChatHandler");

  /**
   * Constructor for TextChatHandler.
   * @param sessionID ID of the stream session.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public TextChatHandler(String sessionID, String tutorID, int token,
      HashMap<Integer, ClientHandler> activeClients) {
    setDaemon(true);
    setName("TextChatHandler-" + token);
    // Assign unique session ID and tutor ID to new text chat handler.
    this.sessionID = sessionID;
    this.tutorID = tutorID;
    this.activeClients = activeClients;

    // Add tutor to session users.
    this.sessionUsers = new ArrayList<Integer>();
    this.jsonQueue = new ArrayList<JsonObject>();
    addUser(token);
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