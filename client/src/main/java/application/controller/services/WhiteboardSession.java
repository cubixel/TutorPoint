package application.controller.services;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class WhiteboardSession {

  private String sessionID;
  private String userID;
  private String mouseState;
  private String canvasTool;
  private Color strokeColor;
  private int strokeWidth;
  private Point2D strokePos;
  private String textField;
  private Color textColor;

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
    this.strokeColor = Color.BLACK;
    this.strokeWidth = 10;
    this.strokePos = new Point2D(-1,-1);
    this.textField = "";
    this.textColor = Color.BLACK;
  }

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

  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }
}
