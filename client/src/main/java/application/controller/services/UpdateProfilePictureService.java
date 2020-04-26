package application.controller.services;

import application.controller.enums.FileUploadResult;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProfilePictureService extends Service<FileUploadResult> {

  private File file;
  private MainConnection connection;
  private volatile boolean finished = false;
  private static final Logger log = LoggerFactory.getLogger("UpdateProfilePictureService");

  public UpdateProfilePictureService(File file, MainConnection connection) {
    this.connection = connection;
    this.file = file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  private FileUploadResult register() {
    finished = false;
    //noinspection StatementWithEmptyBody
    while (!connection.claim()) {
      /* This is checking that the MainConnection
       * is not currently in use. This is to avoid
       * clashes between threads using the
       * DataInput/OutputStreams at the same time. */
    }

    try {
      connection.sendString("ProfileImage");
      connection.sendFile(file);
      String serverReply = connection.listenForString();
      connection.release();
      finished = true;
      return new Gson().fromJson(serverReply, FileUploadResult.class);
    } catch (IOException e) {
      connection.release();
      finished = true;
      return FileUploadResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      connection.release();
      finished = true;
      return  FileUploadResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  @Override
  protected Task<FileUploadResult> createTask() {
    return new Task<FileUploadResult>() {
      @Override
      protected FileUploadResult call() throws Exception {
        return register();
      }
    };
  }

}
