package application.controller.services;

import application.controller.enums.SubjectRequestResult;
import application.model.Subject;
import application.model.SubjectRequest;
import application.model.managers.SubjectManager;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SubjectRequestService extends Service<SubjectRequestResult> {

  MainConnection connection;
  SubjectRequest subjectRequest;
  SubjectManager subjectManager;
  Subject subjectResult;

  /**
   * CONSTRUCTOR DESCRIPTION.
   * @param connection  DESCRIPTION
   */
  public SubjectRequestService(MainConnection connection, SubjectManager subjectManager) {
    this.connection = connection;
    this.subjectManager = subjectManager;
  }

  /**
   * Sends five requests for subjects and appends the results to the
   * subject manager. If no more subjects are left it breaks out the loop.
   * @return  DESCRIPTION
   */
  private SubjectRequestResult fetchSubject() {
    SubjectRequestResult srs;
    subjectRequest = new SubjectRequest(subjectManager.getNumberOfSubjects());

    for (int i = 0; i < 5; i++) {
      try {
        connection.sendString(connection.packageClass(this.subjectRequest));
        String serverReply = connection.listenForString();
        srs = new Gson().fromJson(serverReply, SubjectRequestResult.class);
        if (srs == SubjectRequestResult.SUCCESS) {
          subjectResult = connection.listenForSubject();
          subjectManager.addSubject(subjectResult);
        } else {
          return srs;
        }
      } catch (IOException e) {
        e.printStackTrace();
        return SubjectRequestResult.FAILED_BY_NETWORK;
      }
    }
    return SubjectRequestResult.SUCCESS;
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
