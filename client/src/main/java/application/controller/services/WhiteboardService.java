package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardService extends Thread {

  private MainConnection connection;
  private WhiteboardSession outwardsSession;
  private WhiteboardSession inwardsSession;
  private Whiteboard whiteboard;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");

  public WhiteboardService(MainConnection mainConnection, Whiteboard whiteboard, String userID,
      String sessionID) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.outwardsSession = new WhiteboardSession(userID, sessionID);
    this.inwardsSession = new WhiteboardSession(userID, sessionID);
  }

  @Override
  public void run() {
  }

  private WhiteboardRenderResult sendSessionPackage() {
    try {
      connection.sendString(connection.packageClass(outwardsSession));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, WhiteboardRenderResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRenderResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRenderResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  /**
   * Creates a whiteboard session package to send across to the server.
   */
  public void createSessionPackage(String mouseState, String canvasTool, Color stroke,
      int strokeWidth, Point2D startPos, Point2D endPos) {
    outwardsSession.setMouseState(mouseState);
    outwardsSession.setCanvasTool(canvasTool);
    outwardsSession.getStrokeColor(stroke);
    outwardsSession.setStrokeWidth(strokeWidth);
    outwardsSession.setStrokePositions(startPos, endPos);
  }

  public void updateWhiteboardSession(JsonObject sessionPackage) {

    // Update the whiteboard handler's state and parameters.
    String mouseState =  sessionPackage.get("mouseState").getAsString();
    log.debug(""+mouseState);
    String canvasTool =  sessionPackage.get("canvasTool").getAsString();
    log.debug(""+canvasTool);
    Color stroke =  new Gson().fromJson(sessionPackage.getAsJsonObject("stroke"), Color.class);
    log.debug(""+stroke.toString());
    int strokeWidth =  sessionPackage.get("strokeWidth").getAsInt();
    log.debug(""+strokeWidth);
    Point2D startPos = new Gson().fromJson(sessionPackage.getAsJsonObject("startPos"), Point2D.class);
    log.debug(""+startPos.toString());
    Point2D endPos =  new Gson().fromJson(sessionPackage.getAsJsonObject("endPos"), Point2D.class);
    log.debug(""+endPos.toString());

    // User presses mouse on canvas.
    if (inwardsSession.getMouseState().equals("idle") && mouseState.equals("active")) {
      whiteboard.getGraphicsContext().setStroke(stroke);
      whiteboard.getGraphicsContext().setLineWidth(strokeWidth);
      whiteboard.createNewStroke();

      // User drags mouse on canvas.
    } else if (inwardsSession.getMouseState().equals("active") && mouseState.equals("active")) {
      whiteboard.getGraphicsContext().lineTo(startPos.getX(), startPos.getY());
      whiteboard.draw(startPos);

      // User releases mouse on canvas.
    } else if (inwardsSession.getMouseState().equals("active") && mouseState.equals("idle")) {
      whiteboard.endNewStroke();
    }

    inwardsSession.setMouseState(mouseState);

  }

  /**
   * Creates and sends a session package for the local whiteboard to the server whiteboard handler.
   * @param mousePos User input.
   */
  public void sendPackage(Point2D mousePos, String mouseState, String canvasTool) {
    createSessionPackage(mouseState, canvasTool, whiteboard.getStrokeColor(),
        whiteboard.getStrokeWidth(), mousePos, mousePos);
    WhiteboardRenderResult result = sendSessionPackage();
    // TODO - Anchor Point
    switch (result) {
      case WHITEBOARD_RENDER_SUCCESS:
        log.info("Whiteboard Session Package - Received.");
        break;
      case FAILED_BY_INCORRECT_USER_ID:
        log.warn("Whiteboard Session Package - Wrong user ID.");
        break;
      case FAILED_BY_UNEXPECTED_ERROR:
        log.warn("Whiteboard Session Package - Unexpected error.");
        break;
      case FAILED_BY_NETWORK:
        log.warn("Whiteboard Session Package - Network error.");
        break;
      default:
        log.warn("Whiteboard Session Package - Unknown error.");
    }
  }
}



