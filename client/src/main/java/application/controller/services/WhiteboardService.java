package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Objects;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardService extends Thread {

  private MainConnection connection;
  private WhiteboardSession session;
  private Whiteboard whiteboard;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");


  public WhiteboardService(MainConnection mainConnection, Whiteboard whiteboard, String userID, String sessionID) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.session = new WhiteboardSession(userID, sessionID);
  }

  @Override
  public void run() {
    listenForUpdates();
  }

  private WhiteboardRenderResult sendSessionPackage() {
    try {
      connection.sendString(connection.packageClass(session));
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
    session.setMouseState(mouseState);
    session.setCanvasTool(canvasTool);
    session.getStrokeColor(stroke);
    session.setStrokeWidth(strokeWidth);
    session.setStrokePositions(startPos, endPos);
  }

  private void listenForUpdates() {
    // TODO - loop on the second DOS, when update is found, apply to client.
    try {
      //If null, dont worry about it fam.
      GraphicsContext gc = Objects.requireNonNullElse(connection.listenForWhiteboard(),
          whiteboard.getGraphicsContext());
      whiteboard.setGraphicsContext(gc);
    } catch (IOException e) {
      log.warn(e.toString());
    }
  }

  /**
   * Creates and sends a session package for the local whiteboard to the server whiteboard handler.
   * @param mouseEvent User input.
   *
  public void sendPackage(MouseEvent mouseEvent) {
    Point2D strokePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    whiteboardService.createSessionPackage(mouseState, canvasTool, whiteboard.getStrokeColor(),
        whiteboard.getStrokeWidth(), strokePos, strokePos);
    // TODO - Anchor Point

    if (!whiteboardService.isRunning()) {
      whiteboardService.reset();
      whiteboardService.start();
    }

    whiteboardService.setOnSucceeded(event -> {
      WhiteboardRenderResult result = whiteboardService.getValue();
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
    });
  }*/


//  public void setWhiteboardImage(Image image) {
//    whiteboard.getGraphicsContext().drawImage(image, 0, 0);
//  }
}
