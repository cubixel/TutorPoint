package application.controller.services;

import application.controller.enums.TutorRequestResult;
import application.model.Account;
import application.model.managers.TutorManager;
import application.model.requests.TopTutorsRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorRequestService extends Service<TutorRequestResult> {

  private MainConnection connection;
  private TutorManager tutorManager;

  private static final Logger log = LoggerFactory.getLogger("TutorRequestService");

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param connection DESCRIPTION
   */
  public TutorRequestService(MainConnection connection, TutorManager tutorManager) {
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
    TutorRequestResult trr;
    TopTutorsRequest topTutorsRequest = new TopTutorsRequest(tutorManager.getNumberOfTutors());
    try {
      connection.sendString(connection.packageClass(topTutorsRequest));
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
          return trr;
        }
      } catch (IOException e) {
        log.error("Error listening for server response", e);
        return TutorRequestResult.FAILED_BY_NETWORK;
      }
    }
    return TutorRequestResult.TUTOR_REQUEST_SUCCESS;
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
