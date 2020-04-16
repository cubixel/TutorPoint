package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.controller.enums.WhiteboardRequestResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the interactive whiteboard service used
 * to send and receive whiteboard session packages
 * to draw on the clients whiteboard.
 *
 * @author Oliver Still
 * @author Che McKirgan
 */
public class WhiteboardService extends Thread {

  private MainConnection connection;
  private WhiteboardSession sessionPackage;
  private Whiteboard whiteboard;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");

  /**
   * Main class constructor for new session.
   *
   * @param mainConnection Main connection of client.
   * @param whiteboard Client's model whiteboard.
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardService(MainConnection mainConnection, Whiteboard whiteboard, String userID,
      String sessionID) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.sessionPackage = new WhiteboardSession(userID, sessionID);
  }

  /**
   * Main class constructor for existing session.
   *
   * @param mainConnection Main connection of client.
   * @param whiteboard Client's model whiteboard.
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardService(MainConnection mainConnection, Whiteboard whiteboard, String userID,
      String sessionID, ArrayList<JsonObject> sessionHistory) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.sessionPackage = new WhiteboardSession(userID, sessionID);
  }

  @Override
  public void run() {
    // TODO - Nothing to run?
  }

  private WhiteboardRenderResult sendSessionPackage() {
    try {
      connection.sendString(connection.packageClass(sessionPackage));
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
   * Creates and sends a session package for the
   * local whiteboard to the server whiteboard handler.
   *
   * @param canvasTool The selected tool name.
   * @param mouseState The state of the client's mouse ('idle'/'active').
   * @param mousePos The 2D coordinates of the mouse on the canvas.
   */
  public void sendSessionUpdates(String canvasTool, String mouseState, Point2D mousePos) {

    // Create session package to send to server.
    sessionPackage.setMouseState(mouseState);
    sessionPackage.setCanvasTool(canvasTool);
    sessionPackage.setStrokeColor(whiteboard.getStrokeColor());
    sessionPackage.setStrokeWidth(whiteboard.getStrokeWidth());
    sessionPackage.setStrokePosition(mousePos);
    sessionPackage.setTextField(whiteboard.getTextField());
    sessionPackage.setTextColor(whiteboard.getTextColor());

    // Send package to server
    WhiteboardRenderResult result = sendSessionPackage();
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
        sendSessionPackage();
        break;
      default:
        log.warn("Whiteboard Session Package - Unknown error.");
    }
  }

  /**
   * Method to update the client whiteboard model using the
   * received session package.
   *
   * @param sessionPackage Received session package.
   */
  public void updateWhiteboardSession(JsonObject sessionPackage) {

    // Update the whiteboard handler's state and parameters.
    String mouseState = sessionPackage.get("mouseState").getAsString();
    String canvasTool = sessionPackage.get("canvasTool").getAsString();
    int strokeWidth = sessionPackage.get("strokeWidth").getAsInt();
    Color strokeColor = new Gson().fromJson(sessionPackage.getAsJsonObject("strokeColor"),
        Color.class);
    Point2D mousePos = new Gson().fromJson(sessionPackage.getAsJsonObject("strokePos"),
        Point2D.class);
    String textField = sessionPackage.get("textField").getAsString();
    Color textColor = new Gson().fromJson(sessionPackage.getAsJsonObject("textColor"), Color.class);

    // Set stroke color and width remotely.
    this.whiteboard.setStrokeColor(new Color(strokeColor.getRed(), strokeColor.getGreen(),
        strokeColor.getBlue(), strokeColor.getOpacity()));
    this.whiteboard.setTextColor(new Color(textColor.getRed(), textColor.getGreen(),
        textColor.getBlue(), textColor.getOpacity()));
    this.whiteboard.setStrokeWidth(strokeWidth);
    this.whiteboard.setTextField(textField);

    log.debug(sessionPackage.toString());

    // Draw to canvas remotely.
    this.whiteboard.draw(canvasTool, mouseState, mousePos);
  }
}
