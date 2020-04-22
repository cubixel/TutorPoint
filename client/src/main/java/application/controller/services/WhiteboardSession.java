package application.controller.services;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * This class is used to package the current information
 * about the user drawing on the whiteboard to be sent
 * by the service.
 *
 * @author Oliver Still
 */
public class WhiteboardSession {

  private int sessionID;
  private int userID;
  private String mouseState;
  private String prevMouseState;
  private String canvasTool;
  private Color strokeColor;
  private int strokeWidth;
  private Point2D strokePos;
  private String textField;
  private Color textColor;
  private boolean studentAccess;

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
    this.prevMouseState = "idle";
    this.canvasTool = "pen";
    this.strokeColor = Color.BLACK;
    this.strokeWidth = 10;
    this.strokePos = new Point2D(-1,-1);
    this.textField = "";
    this.textColor = Color.BLACK;
    this.studentAccess = false;
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

  public String getMouseState() {
    return mouseState;
  }

  public void setPrevMouseState(String prevMouseState) {
    this.prevMouseState = prevMouseState;
  }

  public void setCanvasTool(String canvasTool) {
    this.canvasTool = canvasTool;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }

  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }

  public void setStudentAccess(boolean studentAccess) {
    this.studentAccess = studentAccess;
  }
}
