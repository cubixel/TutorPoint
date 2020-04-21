package application.controller.services;

import application.model.Message;

/**
 * This class is used to package the current message
 * of the text chat to be sent
 * by the TextChatService.
 *
 * @author Oliver Clarke
 */
public class TextChatSession {

  private String sessionID;
  private String userID;
  private Message message;

  /**
   * Constructor for Student.
   * @param userID .
   * @param sessionID .
   * @param message
   */
  public TextChatSession(String userID, String sessionID, Message  message) {
    this.sessionID = sessionID;
    this.userID = userID;
    this.message = message;
  }
}