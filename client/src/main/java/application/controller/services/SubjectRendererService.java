package application.controller.services;

import application.model.managers.SubjectManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.HBox;

public class SubjectRendererService extends Service<Void> {

  private MainConnection connection;
  private SubjectManager subjectManager;
  private HBox horizontalBox;

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public SubjectRendererService(MainConnection connection, HBox horizontalBox) {
    this.connection = connection;
    subjectManager = new SubjectManager();
    this.horizontalBox = horizontalBox;
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  private void fetchSubjects() {
    try {
      connection.sendString("SubjectRequest");
      String serverReply = connection.listenForString();
      //return new Gson().fromJson(serverReply, AccountRegisterResult.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  @Override
  protected Task<Void> createTask() {
    return new Task<>() {
      @Override
      protected Void call() throws Exception {
        try {
          fetchSubjects();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    };
  }
}
