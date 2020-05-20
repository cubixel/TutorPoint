package application.controller.services;

import application.controller.enums.FollowTutorResult;
import application.model.requests.FollowSubjectRequest;
import application.model.requests.FollowTutorRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the FollowTutorRequestService used
 * to send send a follow/un-follow request to the server
 * regarding tutors. After sending the request it waits
 * for a response from the server.
 *
 * @author James Gardner
 * @see FollowSubjectRequest
 */
public class FollowTutorRequestService extends Service<FollowTutorResult> {

  private final MainConnection connection;
  private final int tutorID;
  private boolean isFollowing;
  private volatile boolean finished = false;

  private static final Logger log = LoggerFactory.getLogger("FollowTutorRequestService");

  /**
   * Main class constructor used to create the service.
   *
   * @param connection
   *        Main connection of client
   *
   * @param tutorID
   *        A unique ID that is assigned to a user upon creation
   *
   * @param isFollowing
   *        Is the user is currently following the subject or not
   */
  public FollowTutorRequestService(MainConnection connection, int tutorID, boolean isFollowing) {
    this.connection = connection;
    this.tutorID = tutorID;
    this.isFollowing = isFollowing;
  }

  /**
   * This packages up the {@code FollowTutorRequest} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * String with information on if the request process was successful or why it wasn't.
   * To reduce the chances of clashing on the MainConnection with different threads a
   * finished Boolean has been intoduced and the {@code MainConnection.claim()} and
   * {@code MainConnection.release()} methods are used.
   *
   * @return {@code FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS} if the update was successful,
   *         otherwise various other {@code FollowTutorResult} will explain the issue.
   */
  private FollowTutorResult followTutor() {
    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }

    FollowTutorRequest followTutorRequest = new FollowTutorRequest(tutorID, isFollowing);

    try {
      connection.sendString(connection.packageClass(followTutorRequest));
    } catch (IOException e) {
      log.error("Could not send request", e);
      connection.release();
      finished = true;
      return FollowTutorResult.FAILED_BY_NETWORK;
    }
    try {
      String serverReply = connection.listenForString();
      connection.release();
      finished = true;
      return new Gson().fromJson(serverReply, FollowTutorResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      connection.release();
      finished = true;
      return FollowTutorResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
      connection.release();
      finished = true;
      return FollowTutorResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFollowing(boolean following) {
    isFollowing = following;
  }

  @Override
  protected Task<FollowTutorResult> createTask() {
    return new Task<>() {
      @Override
      protected FollowTutorResult call() {
        return followTutor();
      }
    };
  }
}
