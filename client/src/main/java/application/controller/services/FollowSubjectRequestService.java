package application.controller.services;

import application.controller.enums.FollowSubjectResult;
import application.model.requests.FollowSubjectRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the FollowSubjectRequestService used
 * to send send a follow/un-follow request to the server
 * regarding subjects. After sending the request it waits
 * for a response from the server.
 *
 * @author James Gardner
 * @see FollowSubjectRequest
 */
public class FollowSubjectRequestService extends Service<FollowSubjectResult> {

  private final MainConnection connection;
  private final int subjectID;
  private boolean isFollowing;
  private volatile boolean finished = false;

  private static final Logger log = LoggerFactory.getLogger("FollowTutorRequestService");

  /**
   * Main class constructor used to create the service.
   *
   * @param connection
   *        Main connection of client
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param isFollowing
   *        Is the user is currently following the subject or not
   */
  public FollowSubjectRequestService(MainConnection connection, int subjectID,
      boolean isFollowing) {
    this.connection = connection;
    this.subjectID = subjectID;
    this.isFollowing = isFollowing;
  }

  /**
   * This packages up the {@code FollowSubjectRequest} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * String with information on if the request process was successful or why it wasn't.
   * To reduce the chances of clashing on the MainConnection with different threads a
   * finished Boolean has been intoduced and the {@code MainConnection.claim()} and
   * {@code MainConnection.release()} methods are used.
   *
   * @return {@code FollowSubjectResult.FOLLOW_SUBJECT_RESULT_SUCCESS} if the update was successful,
   *         otherwise various other {@code FollowSubjectResult} will explain the issue.
   */
  private FollowSubjectResult followSubject() {
    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }

    FollowSubjectRequest followSubjectRequest = new FollowSubjectRequest(subjectID, isFollowing);

    try {
      connection.sendString(connection.packageClass(followSubjectRequest));
    } catch (IOException e) {
      log.error("Could not send request", e);
      connection.release();
      finished = true;
      return FollowSubjectResult.FAILED_BY_NETWORK;
    }
    try {
      String serverReply = connection.listenForString();
      connection.release();
      finished = true;
      return new Gson().fromJson(serverReply, FollowSubjectResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      connection.release();
      finished = true;
      return FollowSubjectResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
      connection.release();
      finished = true;
      return FollowSubjectResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFollowing(boolean following) {
    isFollowing = following;
  }

  @Override
  protected Task<FollowSubjectResult> createTask() {
    return new Task<>() {
      @Override
      protected FollowSubjectResult call() {
        return followSubject();
      }
    };
  }
}