import com.google.gson.JsonObject;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WhiteboardHandler extends Thread {

  private Canvas canvas;
  private String sessionID;
  private String tutorID;
  private String mouseState;
  private String canvasTool;
  private boolean tutorOnlyAccess;
  private Color stroke;
  private int strokeWidth;
  private double strokeXPosition;
  private double strokeYPosition;
  private ArrayList<String> sessionUsers;

  /**
   * Constructor for WhiteboardHandler.
   * @param sessionID ID of the stream session.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public WhiteboardHandler(String sessionID, String tutorID) {
    // Assign unique session ID and tutor ID to new whiteboard handler.
    this.sessionID = sessionID;
    this.tutorID = tutorID;

    // Set whiteboard defaults.
    this.mouseState = "idle";
    this.canvasTool = "pen";
    this.tutorOnlyAccess = true;
    this.stroke = Color.BLACK;
    this.strokeWidth = -1;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;

    // Add tutor to session users.
    this.sessionUsers = new ArrayList<>();
    addUser(this.tutorID);
  }

  public void addUser(String userID) {
    this.sessionUsers.add(userID);
  }

  private void parseSessionJson(JsonObject updatePackage) {

    try {
      // Format the JSON package to a JSON object.
      JSONObject jsonObject = (JSONObject) new JSONParser().parse(updatePackage.getAsString());

      // Only allow the tutor to update the whiteboard access control.
      if (this.tutorID.equals(jsonObject.get("userID"))) {
        this.tutorOnlyAccess = (boolean) jsonObject.get("tutorOnlyAccess");
      }

      // Update the whiteboard handler's state and parameters.
      this.mouseState = (String) jsonObject.get("mouseState");
      this.canvasTool = (String) jsonObject.get("canvasTool");
      this.stroke = (Color) jsonObject.get("stroke");
      this.strokeWidth = (int) jsonObject.get("strokeWidth");
      this.strokeXPosition = (int) jsonObject.get("strokeXPosition");
      this.strokeYPosition = (int) jsonObject.get("strokeYPosition");

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public void updateWhiteboard(JsonObject sessionPackage) {
    // Allow tutor to update whiteboard regardless of access control.
    if (this.tutorID.equals(sessionPackage.get("userID").getAsString())) {
      parseSessionJson(sessionPackage);

    // Allow other users to update whiteboard is access control is granted.
    } else if (tutorOnlyAccess) {
      parseSessionJson(sessionPackage);
    }
  }

  public ArrayList<String> getSessionUsers() {
    return this.sessionUsers;
  }

  public String getSessionID() {
    return sessionID;
  }

  public String getTutorID() {
    return tutorID;
  }
}
