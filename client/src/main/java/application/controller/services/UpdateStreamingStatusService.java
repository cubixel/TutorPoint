package application.controller.services;

import application.controller.enums.StreamingStatusUpdateResult;
import application.model.requests.UpdateStreamStatusRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the UpdateStreamingStatusService used
 * to update the server on the streaming status of the
 * tutor. After sending the request it waits
 * for a response from the server.
 *
 * @author James Gardner
 * @see UpdateStreamStatusRequest
 * @see application.controller.StreamWindowController
 */
public class UpdateStreamingStatusService extends Service<StreamingStatusUpdateResult> {

  private final MainConnection connection;
  private volatile boolean finished = false;
  private final UpdateStreamStatusRequest updateStreamStatusRequest;
  private static final Logger log = LoggerFactory.getLogger("UpdateStreamingStatusService");

  /**
   * Main class constructor used to create the service.
   *
   * @param mainConnection
   *        Main connection of client

   * @param isLive
   *        Is the user is currently live streaming or not
   */
  public UpdateStreamingStatusService(MainConnection mainConnection, Boolean isLive) {
    this.connection = mainConnection;
    this.updateStreamStatusRequest = new UpdateStreamStatusRequest(isLive);
  }

  /**
   * This packages up the {@code UpdateStreamStatusRequest} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * String with information on if the request process was successful or why it wasn't.
   * To reduce the chances of clashing on the MainConnection with different threads a
   * finished Boolean has been intoduced and the {@code MainConnection.claim()} and
   * {@code MainConnection.release()} methods are used.
   *
   * @return {@code StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS} if the update was successful,
   *         otherwise various other {@code StreamingStatusUpdateResult} will explain the issue.
   */
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
      connection.sendString(connection.packageClass(updateStreamStatusRequest));
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

  public boolean isFinished() {
    return finished;
  }

  @Override
  protected Task<StreamingStatusUpdateResult> createTask() {
    return new Task<>() {
      @Override
      protected StreamingStatusUpdateResult call() {
        return update();
      }
    };
  }
}