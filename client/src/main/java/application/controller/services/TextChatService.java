package application.controller.services;

import application.controller.enums.TextChatMessageResult;
import application.model.Message;
import application.model.managers.MessageManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION: This class is used to manage the text chat connection to the server for
 * sending and receiving messages.
 *
 * @author Oli Clarke
 */

public class TextChatService extends Thread {

  private MainConnection connection;        // Connection to server.
  private Message message;                  // Message for service.
  private MessageManager messageManager;    // Locally stored messages.
  private TextChatSession sessionPackage;   // Session packages of current connected text chat.
  private static final Logger log = LoggerFactory.getLogger("TextChatService");

  /**
   * Main class constructor.
   */
  public TextChatService(Message message, MessageManager messageManager, MainConnection connection,
      String userName, Integer userID, Integer sessionID) {
    this.message = message;
    this.messageManager = messageManager;
    this.connection = connection;
    this.sessionPackage = new TextChatSession(userName, userID, sessionID, message);
  }

  /**
   * Method for sending messsages to server.
   */
  public TextChatMessageResult send() {

    // Sends session package to server and waits for reply.
    try {
      connection.sendString(connection.packageClass(this.sessionPackage));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, TextChatMessageResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return TextChatMessageResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return TextChatMessageResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  /**
   * Creates and sends a session package for the local text chat to the server text chat handler.
   */
  public void sendSessionUpdates(Message message) {

    // Create session package to send to server.
    sessionPackage.setMessage(message);

    // Send package to server
    TextChatMessageResult result = send();
    switch (result) {
      case TEXT_CHAT_MESSAGE_SUCCESS:
        log.info("TextChat Session Package - Received.");
        break;
      case FAILED_BY_INCORRECT_USER_ID:
        log.warn("TextChat Session Package - Wrong user ID.");
        break;
      case FAILED_BY_UNEXPECTED_ERROR:
        log.warn("TextChat Session Package - Unexpected error.");
        break;
      case FAILED_BY_NETWORK:
        log.warn("TextChat Session Package - Network error.");
        send();
        break;
      default:
        log.warn("TextChat Session Package - Unknown error.");
    }
  }

  /**
   * Method to update the client text chat model using the received session package.
   *
   * @param sessionPackage Received session package.
   */
  public void updateTextChatSession(JsonObject sessionPackage) {

    Message sessionMessage = new Gson()
        .fromJson(sessionPackage.getAsJsonObject("message"), Message.class);

    // Extract message from session package.
    this.message = sessionMessage;
    log.debug(this.message.getMsg());

    // Add message to local list of stored messages.
    messageManager.addMessage(this.message);

    //log.debug(sessionPackage.toString()); //TODO check this out
  }

  /**
   * GETTERS & SETTERS.
   **/

  public void setMessage(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }
}