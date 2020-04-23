package application.controller.services;

import application.controller.enums.WhiteboardRequestResult;
import application.model.Whiteboard;
import application.model.requests.WhiteboardRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the session request service used
 * to query the server if a session is already running
 * or if one needs to be created.
 *
 * @author Oliver Still
 * @author Che McKirgan
 */
public class WhiteboardRequestService extends Service<WhiteboardRequestResult> {

  private MainConnection connection;
  private Whiteboard whiteboard;
  private WhiteboardService whiteboardService;
  private WhiteboardRequestSession sessionRequest;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");

  /**
   * Main class constructor.
   *
   * @param mainConnection Main connection of client.
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardRequestService(MainConnection mainConnection, Whiteboard whiteboard,
      WhiteboardService whiteboardService, int userID, int sessionID) {
    this.connection = mainConnection;
    this.whiteboard = whiteboard;
    this.whiteboardService = whiteboardService;
    this.sessionRequest = new WhiteboardRequestSession(userID, sessionID);
  }

  private WhiteboardRequestResult requestSession() {
    try {
      connection.sendString(connection.packageClass(sessionRequest));
      String serverReply = connection.listenForString();
      log.debug("***2 " + serverReply);
      WhiteboardRequest response = new Gson().fromJson(serverReply, WhiteboardRequest.class);
      if (response.isSessionExists()) {
        whiteboard.setStudentAccess(response.isStudentAccess());
        whiteboard.setTutorID(response.getSessionID());
        for (JsonObject history : response.getSessionHistory()) {
          whiteboardService.updateWhiteboardSession(history);
        }
        return WhiteboardRequestResult.SESSION_REQUEST_TRUE;
      } else {
        return WhiteboardRequestResult.SESSION_REQUEST_FALSE;
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRequestResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRequestResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

  @Override
  protected Task<WhiteboardRequestResult> createTask() {
    return new Task<WhiteboardRequestResult>() {
      @Override
      protected WhiteboardRequestResult call() throws Exception {
        return requestSession();
      }
    };
  }
}
