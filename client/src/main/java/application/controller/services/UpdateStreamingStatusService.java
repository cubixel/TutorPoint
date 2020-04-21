package application.controller.services;

import application.controller.enums.StreamingStatusUpdateResult;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateStreamingStatusService extends Service<StreamingStatusUpdateResult> {

  MainConnection connection;

  public UpdateStreamingStatusService(MainConnection mainConnection) {
    this.connection = mainConnection;
  }

  private StreamingStatusUpdateResult update() {
    /*
     * Packages the created account and sends it to the server, waits up to 3s for a reply,
     * if no reply is given network failure is assumed.
     */

    //TODO: Receive login token is this needed??
    try {
      connection.sendString("ChangeStatus");
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, StreamingStatusUpdateResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return StreamingStatusUpdateResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return StreamingStatusUpdateResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  @Override
  protected Task<StreamingStatusUpdateResult> createTask() {
    return new Task<StreamingStatusUpdateResult>() {
      @Override
      protected StreamingStatusUpdateResult call() {
        return update();
      }
    };
  }
}