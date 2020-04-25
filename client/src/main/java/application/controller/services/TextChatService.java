package application.controller.services;

import application.controller.enums.TextChatRequestResult;
import application.model.Message;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Task;

public class TextChatService extends Thread {

  private MainConnection connection;
  private Message message;
  private TextChatSession sessionPackage;

  public TextChatService(Message message, MainConnection connection, String userID, String sessionID) {
    this.message = message;
    this.connection = connection;
    this.sessionPackage = new TextChatSession(userID, sessionID, message);
  }

  /**
   * Method for sending message object and waiting on server reply.
   */
  public TextChatRequestResult send() {
    // Send message to server and waits reply.
    try {
      connection.sendString(connection.packageClass(this.message));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, TextChatRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return TextChatRequestResult.NETWORK_FAILURE;
    } catch (Exception e) {
      e.printStackTrace();
      return TextChatRequestResult.UNKNOWN_ERROR;
    }
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  protected Task<TextChatRequestResult> createTask() {
    return new Task<TextChatRequestResult>() {
      @Override
      protected TextChatRequestResult call() throws Exception {
        return send();
      }
    };
  }

}
