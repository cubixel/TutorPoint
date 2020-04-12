package application.controller.services;

import application.controller.enums.WhiteboardRequestResult;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardRequestService extends Service<WhiteboardRequestResult> {

  private MainConnection connection;
  private WhiteboardRequestSession sessionRequest;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardService");


  public WhiteboardRequestService(MainConnection mainConnection, String userID, String sessionID) {
    this.connection = mainConnection;
    this.sessionRequest = new WhiteboardRequestSession(userID, sessionID);

  }

  private WhiteboardRequestResult requestSession() {
    try {
      connection.sendString(connection.packageClass(sessionRequest));
      String serverReply = connection.listenForString();
      log.info(new Gson().fromJson(serverReply, WhiteboardRequestResult.class).toString());
      return new Gson().fromJson(serverReply, WhiteboardRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRequestResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.toString());
      return WhiteboardRequestResult.FAILED_BY_SESSION_ID;
    }
  }

  @Override
  protected Task<WhiteboardRequestResult> createTask() {
    return new Task<WhiteboardRequestResult>() {
      @Override
      protected WhiteboardRequestResult call() throws Exception {
        return requestSession();
      }
    };
  }
}
