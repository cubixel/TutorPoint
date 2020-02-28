package application.controller.services;

import application.model.Account;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FileDownloadService extends Service<FileDownloadResult> {

  MainConnection connection;
  FileRequest request;

  public FileDownloadService(MainConnection connection, FileRequest request) {
    this.connection = connection;
    this.request = request;
  }

  private FileDownloadResult fetchFile() {
    try {
      connection.sendString(connection.packageClass(this.request));
      connection.listenForFile();
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, FileDownloadResult.class);

    } catch (IOException e) {
      e.printStackTrace();
      return FileDownloadResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return FileDownloadResult.FAILED_BY_NO_FILE_FOUND;
    }
  }

  @Override
  protected Task<FileDownloadResult> createTask() {
    return new Task<FileDownloadResult>() {
      @Override
      protected FileDownloadResult call() throws Exception {
        return fetchFile();
      }
    };
  }

}
