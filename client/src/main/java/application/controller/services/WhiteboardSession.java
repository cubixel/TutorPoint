package application.controller.services;

import java.util.UUID;
import javafx.scene.paint.Color;

public class WhiteboardSession {

  private String sessionID;
  private String userID;
  private String mouseState;
  private String canvasTool;
  private boolean tutorOnlyAccess;
  private Color stroke;
  private int strokeWidth;
  private double strokeXPosition;
  private double strokeYPosition;

  /**
   * Contructor for WhiteboardSession module.
   * @param userID ID of the tutor hosting the stream.
   */
  public WhiteboardSession(String userID) {
    this.sessionID = UUID.randomUUID().toString();
    this.userID = userID;
    this.mouseState = "idle";
    this.canvasTool = "pen";
    this.tutorOnlyAccess = true;
    this.stroke = Color.BLACK;
    this.strokeWidth = -1;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;

  }

  /**
   * .
   * @param userID .
   * @param sessionID .
   */
  public WhiteboardSession(String userID, String sessionID) {
    this.sessionID = sessionID;
    this.userID = userID;
    this.mouseState = "idle";
    this.canvasTool = "pen";
    this.tutorOnlyAccess = true;
    this.stroke = Color.BLACK;
    this.strokeWidth = 10;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;
  }

  public void setTutorOnlyAccess(boolean tutorOnlyAccess) {
    this.tutorOnlyAccess = tutorOnlyAccess;
  }

  public void getStrokeColor(Color stroke) {
    this.stroke = stroke;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public void setStrokePosition(double xpos, double ypos) {
    this.strokeXPosition = xpos;
    this.strokeYPosition = ypos;
  }

  public void setMouseState(String mouseState) {
    this.mouseState = mouseState;
  }

  public void setCanvasTool(String canvasTool) {
    this.canvasTool = canvasTool;
  }

  public String getSessionID() {
    return sessionID;
  }

  public String getUserID() {
    return userID;
  }

  public String getMouseState() {
    return mouseState;
  }

  public String getCanvasTool() {
    return canvasTool;
  }

  public boolean isTutorOnlyAccess() {
    return tutorOnlyAccess;
  }

  public Color getStroke() {
    return stroke;
  }

  public int getStrokeWidth() {
    return strokeWidth;
  }

  public double getStrokeXPosition() {
    return strokeXPosition;
  }

  public double getStrokeYPosition() {
    return strokeYPosition;
  }
}
