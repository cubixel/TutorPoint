package application.controller.services;

import application.model.Whiteboard;
import java.util.UUID;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WhiteboardSession {

  private String sessionID;
  private String tutorID;
  private boolean tutorOnlyAccess;
  private Color strokeColor;
  private int strokeWidth;
  private int strokeXPosition;
  private int strokeYPosition;
  private String mouseState;

  public WhiteboardSession(String tutorID) {
    this.sessionID = UUID.randomUUID().toString();
    this.tutorID = tutorID;
    this.tutorOnlyAccess = true;
    this.strokeColor = Color.BLACK;
    this.strokeWidth = -1;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;
    this.mouseState = "idle";
  }

  public void setTutorOnlyAccess(boolean tutorOnlyAccess) {
    this.tutorOnlyAccess = tutorOnlyAccess;
  }

  public void setStrokeColor(Color strokeColor) {
    this.strokeColor = strokeColor;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public void setStrokePosition(int xPos, int yPos) {
    this.strokeXPosition = xPos;
    this.strokeYPosition = yPos;
  }

  public void setMouseState(String mouseState) {
    this.mouseState = mouseState;
  }
}
