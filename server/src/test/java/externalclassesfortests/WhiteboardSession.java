package externalclassesfortests;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * CLASSES IN THE 'externalclassesfortests' PACKAGE ARE NOT USED FOR ANYTHING
 * OTHER THAN TESTS THAT NEED ACCESS TO A COPY OF THE CLASS.
 *
 * <p>This class is used to package the current information
 * about the user drawing on the whiteboard to be sent
 * by the service.
 *
 * @author Oliver Still
 */
public class WhiteboardSession {

  private int sessionID;
  private int userID;
  private String mouseState;
  private String canvasTool;
  private Color strokeColor;
  private int strokeWidth;
  private Point2D strokePos;
  private String textField;

  /**
   * Main class constructor.
   *
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardSession(int userID, int sessionID) {
    this.sessionID = sessionID;
    this.userID = userID;
    this.mouseState = "idle";
    this.canvasTool = "pen";
    this.strokeColor = Color.BLACK;
    this.strokeWidth = 10;
    this.strokePos = new Point2D(-1,-1);
    this.textField = "";
  }

  /* Setters and Getters */

  public void setStrokeColor(Color strokeColor) {
    this.strokeColor = strokeColor;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public void setStrokePosition(Point2D startPos) {
    this.strokePos = new Point2D(startPos.getX(), startPos.getY());
  }

  public void setMouseState(String mouseState) {
    this.mouseState = mouseState;
  }

  public void setCanvasTool(String canvasTool) {
    this.canvasTool = canvasTool;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }
}
