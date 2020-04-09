package application.controller.services;

import application.controller.enums.TextRequestResult;
import application.model.Message;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Task;

public class TextService {

  private MainConnection connection;
  private Message message;

  public TextService(Message message, MainConnection connection) {
    this.message = message;
    this.connection = connection;
  }

  /** 
   * Method for sending message object and waiting on server reply.
   */
  public TextRequestResult send() {
    // Send message to server and waits reply.
    try {
      connection.sendString(connection.packageClass(this.message));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, TextRequestResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return TextRequestResult.NETWORK_FAILURE;
    } catch (Exception e) {
      e.printStackTrace();
      return  TextRequestResult.UNKNOWN_ERROR;
    }
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  protected Task<TextRequestResult> createTask() {
    return new Task<TextRequestResult>() {
      @Override
      protected TextRequestResult call() throws Exception {
        return send();
      }
    };
  }

}
