package application.controller.services;

import application.controller.enums.AccountLoginResult;
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
  SubjectRequest request;
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
    for (int i = 0; i < 5; i++) {
      try {
        request = new SubjectRequest(subjectManager.getNumberOfSubjects());
        connection.sendString(connection.packageClass(this.request));
        subjectResult = connection.listenForSubject();
        if (subjectResult == null) {
          return SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS;
        }
        subjectManager.addSubject(subjectResult);
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
