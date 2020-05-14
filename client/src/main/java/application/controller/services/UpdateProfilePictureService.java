package application.controller.services;

import application.controller.enums.FileUploadResult;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the javafx implementation of multithreading (service).
 * It connect to the server, send over the users profile picture and waits
 * for the server to respond with the outcome of the update.
 *
 * @author James Gardner
 */
public class UpdateProfilePictureService extends Service<FileUploadResult> {

  private File file;
  private final MainConnection connection;
  private volatile boolean finished = false;
  private static final Logger log = LoggerFactory.getLogger("UpdateProfilePictureService");

  public UpdateProfilePictureService(File file, MainConnection connection) {
    this.connection = connection;
    this.file = file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  /**
   * Sends the String name of the request followed by the file to the Server.
   * It then tells the connection to listen for String with information on if the upload
   * process was successful or why it wasn't. To reduce the chances of clashing on the
   * MainConnection with different threads a finished Boolean has been intoduced and the
   * {@code MainConnection.claim()} and {@code MainConnection.release()} methods are used.
   *
   * @return {@code FileUploadResult.FILE_UPLOAD_SUCCESS} if the update was successful,
   *         otherwise various other {@code FileUploadResult} will explain the issue.
   */
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
      log.error("FAILED_BY_NETWORK", e);
      connection.release();
      finished = true;
      return FileUploadResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
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
    return new Task<>() {
      @Override
      protected FileUploadResult call() {
        return register();
      }
    };
  }

}
