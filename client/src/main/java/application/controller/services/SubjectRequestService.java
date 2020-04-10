package application.controller.services;

import application.controller.enums.SubjectRequestResult;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.model.requests.SubjectRequest;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION.
 * #################
 *
 * @author James Gardner
 * @see SubjectRequestResult
 * @see MainConnection
 * @see SubjectManager
 */
public class SubjectRequestService extends Service<SubjectRequestResult> {

  private MainConnection connection;
  private SubjectManager subjectManager;

  private static final Logger log = LoggerFactory.getLogger("SubjectRequestService");

  /**
   * CONSTRUCTOR DESCRIPTION.
   * @param connection  DESCRIPTION
   */
  public SubjectRequestService(MainConnection connection, SubjectManager subjectManager) {
    this.connection = connection;
    this.subjectManager = subjectManager;
  }

  /**
   * Sends a requests for five subjects and appends the results to the
   * subject manager. If no more subjects are left it breaks out the loop.
   * @return  DESCRIPTION
   */
  private SubjectRequestResult fetchSubject() {
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }
    SubjectRequestResult srs;
    SubjectRequest subjectRequest = new SubjectRequest(subjectManager.getNumberOfSubjects());
    try {
      connection.sendString(connection.packageClass(subjectRequest));
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
    for (int i = 0; i < 5; i++) {
      try {
        String serverReply = connection.listenForString();
        srs = new Gson().fromJson(serverReply, SubjectRequestResult.class);
        if (srs == SubjectRequestResult.SUBJECT_REQUEST_SUCCESS) {
          Subject subjectResult = connection.listenForSubject();
          subjectManager.addSubject(subjectResult);
        } else {
          connection.release();
          return srs;
        }
      } catch (IOException e) {
        log.error("Error listening for server response", e);
        connection.release();
        return SubjectRequestResult.FAILED_BY_NETWORK;
      }
    }
    connection.release();
    return SubjectRequestResult.SUBJECT_REQUEST_SUCCESS;
  }

  @Override
  protected Task<SubjectRequestResult> createTask() {
    return new Task<SubjectRequestResult>() {
      @Override
      protected SubjectRequestResult call() throws Exception {
        return fetchSubject();
      }
    };
  }

}
