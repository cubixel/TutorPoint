package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WhiteboardService extends Service<WhiteboardRenderResult> {

  private MainConnection connection;
  private WhiteboardSession session;

  public WhiteboardService(MainConnection mainConnection, String tutorID) {
    this.connection = mainConnection;
    this.session = new WhiteboardSession(tutorID);
  }

  private WhiteboardRenderResult sendSessionPackage() {

    // TODO - session.packageWhiteboard(whiteboard);

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

  @Override
  protected Task<WhiteboardRenderResult> createTask() {
    return new Task<WhiteboardRenderResult>() {
      @Override
      protected WhiteboardRenderResult call() throws Exception {
        return sendSessionPackage();
      }
    };
  }
}

