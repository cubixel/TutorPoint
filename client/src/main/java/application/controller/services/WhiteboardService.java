package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

public class WhiteboardService extends Service<WhiteboardRenderResult> {

  private MainConnection connection;
  private WhiteboardSession session;
  private boolean tutorOnlyAccess;

  /**
   * Constructor for WhiteboardService.
   * @param mainConnection Connection port to Server.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public WhiteboardService(MainConnection mainConnection, String tutorID) {
    this.connection = mainConnection;
    this.session = new WhiteboardSession(tutorID);
    this.tutorOnlyAccess = true;
  }

  private WhiteboardRenderResult sendSessionPackage() {
    try {
      connection.sendString(connection.packageClass(session));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, WhiteboardRenderResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return WhiteboardRenderResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return WhiteboardRenderResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  private void receiveSessionPackage() {
    try {
      String serverReply = connection.listenForString();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a whiteboard session package to send across to the server.
   */
  public void createSessionPackage(String mouseState, Color strokeColor,
      int strokeWidth, double xpos, double ypos) {
    session.setMouseState(mouseState);
    session.setStrokeColor(strokeColor);
    session.setStrokeWidth(strokeWidth);
    session.setStrokePosition(xpos, ypos);
    session.setTutorOnlyAccess(this.tutorOnlyAccess);
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

  public void setTutorOnlyAccess(boolean tutorOnlyAccess) {
    this.tutorOnlyAccess = tutorOnlyAccess;
  }
}
