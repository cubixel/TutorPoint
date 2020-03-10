package application.controller.services;

import application.controller.enums.FileDownloadResult;
import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WhiteboardService extends Service<WhiteboardRenderResult> {

  Whiteboard whiteboard;
  MainConnection connection;

  public WhiteboardService(Whiteboard whiteboard, MainConnection mainConnection) {
    this.whiteboard = whiteboard;
    this.connection = mainConnection;
  }

  public void setWhiteboard(Whiteboard whiteboard) {
    this.whiteboard = whiteboard;
  }

  private WhiteboardRenderResult renderCanvas() {

    try {
      connection.sendString(connection.packageClass(this.whiteboard));
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

  @Override
  protected Task<WhiteboardRenderResult> createTask() {
    return new Task<WhiteboardRenderResult>() {
      @Override
      protected WhiteboardRenderResult call() throws Exception {
        return renderCanvas();
      }
    };
  }
}

