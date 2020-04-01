import com.google.gson.JsonObject;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class WhiteboardHandler extends Thread {

  private Canvas canvas;
  private String sessionID;
  private String tutorID;
  private String mouseState;
  private boolean tutorOnlyAccess;
  private Color strokeColor;
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
    this.sessionID = sessionID;
    this.tutorID = tutorID;
    this.mouseState = "idle";
    this.tutorOnlyAccess = true;
    this.strokeColor = Color.BLACK;
    this.strokeWidth = -1;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;
  }

  public void addUser(String userID){
    this.sessionUsers.add(userID);
  }

  private void parseJSON(JsonObject update){

  }

  public void updateWhiteboard(JsonObject update){
    if (tutorOnlyAccess){
      if (this.tutorID.equals(update.get("userID").getAsString())){
        parseJSON(update);
      }
    }else{
      parseJSON(update);
    }
  }

  public ArrayList<String> getSessionUsers(){
    return this.sessionUsers;
  }

  public String getSessionID() {
    return sessionID;
  }

  public String getTutorID() {
    return tutorID;
  }
}
