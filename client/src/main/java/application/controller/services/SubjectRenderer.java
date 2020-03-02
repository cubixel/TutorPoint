package application.controller.services;

import application.model.FileRequest;
import application.model.Subject;
import application.model.managers.SubjectManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.File;
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
  public SubjectRenderer(MainConnection connection, HBox horizontalBox) {
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
      Subject subject;
      File file;

      try {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(serverReply, JsonObject.class);
        String action = jsonObject.get("Class").getAsString();

        if (action.equals("Subject")) {
          subject = new Subject(jsonObject.get("id").getAsInt(), jsonObject.get("name").getAsString(), jsonObject.get("nameOfThumbnailFile").getAsString(), jsonObject.get("thumbnailPath").getAsString());
          FileRequest fr = new FileRequest(subject.getThumbnailPath());
          FileDownloadService fds = new FileDownloadService(connection, fr);

          Platform.runLater(new Runnable(){
            @Override
            public void run() {
              fds.start();
              fds.setOnSucceeded(event ->{
                FileDownloadResult result = fds.getValue();
                switch (result) {
                  case SUCCESS:
                    FileInputStream input = null;
                    try {
                      input = new FileInputStream("client/src/main/resources/application/media/downloads/" + subject.getNameOfThumbnailFile());
                    } catch (FileNotFoundException e) {
                      e.printStackTrace();
                    }
                    Image image = new Image(input);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(130);
                    imageView.setFitWidth(225);
                    horizontalBox.getChildren().add(imageView);
                    break;
                  case FAILED_BY_NO_FILE_FOUND:
                    System.out.println("FAILED_BY_NO_FILE_FOUND");
                    break;
                  case FAILED_BY_NETWORK:
                    System.out.println("FAILED_BY_NETWORK");
                    break;
                }
              });
            }
          });

        } else {
          System.out.println("ERROR Server Returned Something that wasn't a subject");
        }
      } catch (JsonSyntaxException e) {
        e.printStackTrace();
      }
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
