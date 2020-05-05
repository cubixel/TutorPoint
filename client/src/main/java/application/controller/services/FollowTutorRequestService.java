package application.controller.services;

import application.controller.enums.AccountLoginResult;
import application.controller.enums.FollowTutorResult;
import application.controller.enums.TutorRequestResult;
import application.model.managers.TutorManager;
import application.model.requests.FollowTutorRequest;
import application.model.requests.TopTutorsRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowTutorRequestService extends Service<FollowTutorResult> {

  private MainConnection connection;
  private int tutorID;
  private boolean isFollowing;
  private volatile boolean finished = false;

  private static final Logger log = LoggerFactory.getLogger("FollowTutorRequestService");

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param connection DESCRIPTION
   */
  public FollowTutorRequestService(MainConnection connection, int tutorID, boolean isFollowing) {
    this.connection = connection;
    this.tutorID = tutorID;
    this.isFollowing = isFollowing;
  }

  /**
   * Sends a requests for five subjects and appends the results to the subject manager. If no more
   * subjects are left it breaks out the loop.
   *
   * @return DESCRIPTION
   */
  private FollowTutorResult fetchTutors() {
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
    return new Task<FollowTutorResult>() {
      @Override
      protected FollowTutorResult call() throws Exception {
        return fetchTutors();
      }
    };
  }
}
