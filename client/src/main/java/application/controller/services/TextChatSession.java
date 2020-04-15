package application.controller.services;

import application.model.Message;

public class TextChatSession {

  private String sessionID;
  private String userID;
  private Message messages;

  /**
   * Constructor for Student.
   * @param userID .
   * @param sessionID .
   * @param messages
   */
  public TextChatSession(String userID, String sessionID, Message  messages) {
    this.sessionID = sessionID;
    this.userID = userID;
    this.messages = messages;
  }


}
