package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardService extends Service<WhiteboardRenderResult> {

  private MainConnection connection;
  private WhiteboardSession session;
  private Whiteboard whiteboard;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");


  public WhiteboardService(MainConnection mainConnection, Whiteboard whiteboard, String userID, String sessionID) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.session = new WhiteboardSession(userID, sessionID);
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

  @Override
  protected Task<WhiteboardRenderResult> createTask() {
    return new Task<WhiteboardRenderResult>() {
      @Override
      protected WhiteboardRenderResult call() throws Exception {
        return sendSessionPackage();
      }
    };
  }

//  public void setWhiteboardImage(Image image) {
//    whiteboard.getGraphicsContext().drawImage(image, 0, 0);
//  }
}
