package application.controller.services;

import java.util.UUID;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class WhiteboardSession {

  private String sessionID;
  private String userID;
  private String mouseState;
  private String canvasTool;
  private Color stroke;
  private int strokeWidth;
  private Point2D startPos;
  private Point2D endPos;

  /**
   * Constructor for Student.
   * @param userID .
   * @param sessionID .
   */
  public WhiteboardSession(String userID, String sessionID) {
    this.sessionID = sessionID;
    this.userID = userID;
    this.mouseState = "idle";
    this.canvasTool = "pen";
    this.stroke = Color.BLACK;
    this.strokeWidth = 10;
    this.startPos = new Point2D(-1,-1);
    this.endPos = new Point2D(-1,-1);
  }

  public void getStrokeColor(Color stroke) {
    this.stroke = stroke;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public void setStrokePositions(Point2D startPos, Point2D endPos) {
    this.startPos = new Point2D(startPos.getX(), startPos.getY());
    this.endPos = new Point2D(endPos.getX(), endPos.getY());
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

  public Color getStroke() {
    return stroke;
  }

  public int getStrokeWidth() {
    return strokeWidth;
  }

  public Point2D getStartStrokePos() {
    return startPos;
  }

  public Point2D getEndStrokePos() {
    return endPos;
  }

}
