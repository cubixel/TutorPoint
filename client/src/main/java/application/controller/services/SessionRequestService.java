package application.controller.services;

import application.controller.enums.SessionRequestResult;
import application.model.requests.SessionRequest;
import com.google.gson.Gson;
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
 * @author James Gardner
 */
public class SessionRequestService extends Service<SessionRequestResult> {

  private final MainConnection connection;
  private final SessionRequest sessionRequest;
  private volatile boolean finished = false;
  private static final Logger log = LoggerFactory.getLogger("SessionRequestService");

  /**
   * Main class constructor used to create the service.
   *
   * @param mainConnection
   *        Main connection of client
   *
   * @param userID
   *        User ID of the client
   *
   * @param sessionID
   *        Session ID of the stream
   */
  public SessionRequestService(MainConnection mainConnection, int userID, int sessionID,
      Boolean leavingSession, Boolean isHost) {
    this.connection = mainConnection;
    this.sessionRequest = new SessionRequest(userID, sessionID, isHost, leavingSession);
  }

  private SessionRequestResult requestSession() {
    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }

    try {
      connection.sendString(connection.packageClass(sessionRequest));
      String serverReply = connection.listenForString();
      connection.release();
      finished = true;
      return new Gson().fromJson(serverReply, SessionRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.toString());
      connection.release();
      finished = true;
      return SessionRequestResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.toString());
      connection.release();
      finished = true;
      return SessionRequestResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  @Override
  protected Task<SessionRequestResult> createTask() {
    return new Task<>() {
      @Override
      protected SessionRequestResult call() {
        return requestSession();
      }
    };
  }
}
