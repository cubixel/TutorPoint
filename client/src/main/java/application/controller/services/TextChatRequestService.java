package application.controller.services;

import application.controller.enums.TextChatRequestResult;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextChatRequestService extends Service<TextChatRequestResult> {

  private MainConnection connection;
  private TextChatRequestSession sessionRequest;
  private static final Logger log = LoggerFactory.getLogger("TextService");

  public TextChatRequestService(MainConnection mainConnection, Integer userID, Integer sessionID) {
    this.connection = mainConnection;
    this.sessionRequest = new TextChatRequestSession(userID, sessionID);
  }

  // Request text chat session from server and wait for response
  private TextChatRequestResult requestSession() {
    try {
      connection.sendString(connection.packageClass(sessionRequest));
      String serverReply = connection.listenForString();
//      log.info(new Gson().fromJson(serverReply, TextChatRequestResult.class).toString()); //TODO look at this log
      return new Gson().fromJson(serverReply, TextChatRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.toString());
      return TextChatRequestResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.toString());
      return TextChatRequestResult.FAILED_BY_UNKNOWN_ERROR;
    }
  }

    @Override
    protected Task<TextChatRequestResult> createTask() {
      return new Task<TextChatRequestResult>() {
        @Override
        protected TextChatRequestResult call() throws Exception {
          return requestSession();
        }
      };
    }
  }
