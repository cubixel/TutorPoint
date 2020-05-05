package application.controller.services;

import application.controller.enums.FollowSubjectResult;
import application.controller.enums.FollowTutorResult;
import application.model.requests.FollowSubjectRequest;
import application.model.requests.FollowTutorRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowSubjectRequestService extends Service<FollowSubjectResult> {

  private MainConnection connection;
  private int subjectID;
  private boolean isFollowing;
  private volatile boolean finished = false;

  private static final Logger log = LoggerFactory.getLogger("FollowTutorRequestService");

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param connection DESCRIPTION
   */
  public FollowSubjectRequestService(MainConnection connection, int subjectID, boolean isFollowing) {
    this.connection = connection;
    this.subjectID = subjectID;
    this.isFollowing = isFollowing;
  }

  /**
   * Sends a requests for five subjects and appends the results to the subject manager. If no more
   * subjects are left it breaks out the loop.
   *
   * @return DESCRIPTION
   */
  private FollowSubjectResult fetchTutors() {
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
    return new Task<FollowSubjectResult>() {
      @Override
      protected FollowSubjectResult call() throws Exception {
        return fetchTutors();
      }
    };
  }
}
