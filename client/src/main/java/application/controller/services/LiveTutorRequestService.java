package application.controller.services;

import application.controller.enums.TutorRequestResult;
import application.model.Account;
import application.model.managers.TutorManager;
import application.model.requests.LiveTutorsRequest;
import application.model.requests.TopTutorsRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveTutorRequestService extends Service<TutorRequestResult> {

  private MainConnection connection;
  private TutorManager tutorManager;
  private volatile boolean finished = false;

  private static final Logger log = LoggerFactory.getLogger("TutorRequestService");

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param connection DESCRIPTION
   */
  public LiveTutorRequestService(MainConnection connection, TutorManager tutorManager) {
    this.connection = connection;
    this.tutorManager = tutorManager;
  }

  /**
   * Sends a requests for five subjects and appends the results to the subject manager. If no more
   * subjects are left it breaks out the loop.
   *
   * @return DESCRIPTION
   */
  private TutorRequestResult fetchTutors() {
    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }

    TutorRequestResult trr;
    LiveTutorsRequest liveTutorsRequest = new LiveTutorsRequest(tutorManager.getNumberOfTutors());
    try {
      connection.sendString(connection.packageClass(liveTutorsRequest));
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
    for (int i = 0; i < 5; i++) {
      try {
        String serverReply = connection.listenForString();
        trr = new Gson().fromJson(serverReply, TutorRequestResult.class);
        if (trr == TutorRequestResult.TUTOR_REQUEST_SUCCESS) {
          Account accountResult = connection.listenForAccount();
          tutorManager.addTutor(accountResult);
        } else {
          connection.release();
          finished = true;
          return trr;
        }
      } catch (IOException e) {
        log.error("Error listening for server response", e);
        connection.release();
        finished = true;
        return TutorRequestResult.FAILED_BY_NETWORK;
      }
    }
    connection.release();
    finished = true;
    return TutorRequestResult.TUTOR_REQUEST_SUCCESS;
  }

  public boolean isFinished() {
    return finished;
  }

  @Override
  protected Task<TutorRequestResult> createTask() {
    return new Task<TutorRequestResult>() {
      @Override
      protected TutorRequestResult call() throws Exception {
        return fetchTutors();
      }
    };
  }
}
