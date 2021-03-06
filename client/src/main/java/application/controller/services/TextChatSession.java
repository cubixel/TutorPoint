package application.controller.services;

import application.model.Message;

/**
 * CLASS DESCRIPTION: This class is used to package the current message of the text
 * chat to be sent by the TextChatService.
 *
 * @author Oliver Clarke
 */
public class TextChatSession {

  private Integer sessionID;
  private String userName;
  private Integer userID;
  private Message message;

  /**
   * Constructor for Student.
   *
   * @param userID    .
   * @param sessionID .
   * @param message   .
   */
  public TextChatSession(String userName, Integer userID, Integer sessionID, Message message) {
    this.sessionID = sessionID;
    this.userName = userName;
    this.userID = userID;
    this.message = message;
  }

  /**
   * GETTERS & SETTERS.
   **/

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
}