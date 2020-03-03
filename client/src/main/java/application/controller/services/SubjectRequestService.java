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
   *
   * @param connection
   * @param request
   */
  public SubjectRequestService(MainConnection connection, SubjectRequest request, SubjectManager subjectManager) {
    this.connection = connection;
    this.request = request;
    this.subjectManager = subjectManager;
  }

  /**
   *
   * @return
   */
  private SubjectRequestResult fetchSubject() {
    try {
      connection.sendString(connection.packageClass(this.request));
      subjectResult = connection.listenForSubject();
      subjectManager.addSubject(subjectResult);
    } catch (IOException e) {
      e.printStackTrace();
      return SubjectRequestResult.FAILED_BY_NETWORK;
    }
    try {
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, SubjectRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return SubjectRequestResult.FAILED_BY_NETWORK;
    }
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
