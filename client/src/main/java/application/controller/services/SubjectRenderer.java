package application.controller.services;

import application.model.Subject;
import application.model.managers.SubjectManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
          subject = new Subject(jsonObject.get("id").getAsInt(), jsonObject.get("name").getAsString(), jsonObject.get("thumbnailPath").getAsString());
          System.out.println(subject.getThumbnailPath());
          //FileRequest fr = new FileRequest(subject.getThumbnailPath());
          //FileDownloadService fds = new FileDownloadService(connection, fr);
          //fds.start();
          // TODO Figure out how to wait till download complete.
          FileInputStream input = new FileInputStream("client/src/main/resources/application/media/downloads/Maths.png");
          Image image = new Image(input);
          ImageView imageView = new ImageView(image);
          Platform.runLater(new Runnable(){
            @Override
            public void run() {
              // TODO I think this is a hack.
              horizontalBox.getChildren().add(imageView);
            }
          });

        } else {
          System.out.println("bugger");
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
