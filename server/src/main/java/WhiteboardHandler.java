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

  public String getSessionID() {
    return sessionID;
  }

  public String getTutorID() {
    return tutorID;
  }
}
