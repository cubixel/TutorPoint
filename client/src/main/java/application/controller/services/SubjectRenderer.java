package application.controller.services;

import application.controller.enums.FileDownloadResult;
import application.controller.enums.SubjectRequestResult;
import application.model.FileRequest;
import application.model.SubjectRequest;
import application.model.managers.SubjectManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SubjectRenderer extends Service<Void> {

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
  public SubjectRenderer(MainConnection connection, HBox horizontalBox, SubjectManager subjectManager) {
    this.connection = connection;
    this.subjectManager = subjectManager;
    this.horizontalBox = horizontalBox;
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   */
  private void fetchSubjects() {
    SubjectRequest subjectRequest = new SubjectRequest(subjectManager.getNumberOfSubjects());
    SubjectRequestService subjectRequestService = new SubjectRequestService(connection, subjectRequest, subjectManager);
    Platform.runLater(() -> {
      subjectRequestService.start();
      subjectRequestService.setOnSucceeded(srsEvent -> {
        SubjectRequestResult srsResult = subjectRequestService.getValue();
        switch (srsResult) {
          case SUCCESS:
            FileRequest fileRequest = new FileRequest(
                subjectManager.getLastSubject().getThumbnailPath());
            FileDownloadService fileDownloadservice = new FileDownloadService(connection,
                fileRequest);
            fileDownloadservice.start();
            fileDownloadservice.setOnSucceeded(fdsEvent -> {
              FileDownloadResult fdrResult = fileDownloadservice.getValue();
              switch (fdrResult) {
                case SUCCESS:
                  FileInputStream input = null;
                  try {
                    input = new FileInputStream(
                        "client/src/main/resources/application/media/downloads/" + subjectManager.getLastSubject().getNameOfThumbnailFile());
                  } catch (FileNotFoundException e) {
                    e.printStackTrace();
                  }
                  Image image = new Image(input);
                  ImageView imageView = new ImageView(image);
                  imageView.setFitHeight(130);
                  imageView.setFitWidth(225);
                  horizontalBox.getChildren().add(imageView);
                  break;
                case FAILED_BY_FILE_NOT_FOUND:
                  System.out.println("FAILED_BY_NO_FILE_FOUND");
                  break;
                case FAILED_BY_NETWORK:
                  System.out.println("FAILED_BY_NETWORK");
                  break;
              }
            });
            break;
          case FAILED_BY_NETWORK:
            System.out.println("FAILED_BY_NETWORK");
            break;
          case FAILED_BY_NO_MORE_SUBJECTS:
            System.out.println("FAILED_BY_NO_MORE_SUBJECTS");
            break;
        }
      });
    });
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
