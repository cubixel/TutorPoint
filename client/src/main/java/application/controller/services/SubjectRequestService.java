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
   *  METHOD DESCRIPTION.
   * @return  DESCRIPTION
   */
  private SubjectRequestResult fetchSubject() {
    //TODO Lots of error handling success after each one.
    for (int i = 0; i < 5; i++) {
      try {
        request = new SubjectRequest(subjectManager.getNumberOfSubjects());
        connection.sendString(connection.packageClass(this.request));
        subjectResult = connection.listenForSubject();
        subjectManager.addSubject(subjectResult);
      } catch (IOException e) {
        e.printStackTrace();
        return SubjectRequestResult.FAILED_BY_NETWORK;
      }
    }
    //String serverReply = connection.listenForString();
    //return new Gson().fromJson(serverReply, SubjectRequestResult.class);
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
