package application.controller.services;

import application.controller.enums.StreamingStatusUpdateResult;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStreamingStatusService extends Service<StreamingStatusUpdateResult> {

  private MainConnection connection;
  private volatile boolean finished = false;
  private static final Logger log = LoggerFactory.getLogger("UpdateStreamingStatusService");

  public UpdateStreamingStatusService(MainConnection mainConnection) {
    this.connection = mainConnection;
  }

  private StreamingStatusUpdateResult update() {

    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }
    try {
      connection.sendString("ChangeStatus");
      String serverReply = connection.listenForString();
      connection.release();
      finished = true;
      return new Gson().fromJson(serverReply, StreamingStatusUpdateResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      connection.release();
      finished = true;
      return StreamingStatusUpdateResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
      connection.release();
      finished = true;
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