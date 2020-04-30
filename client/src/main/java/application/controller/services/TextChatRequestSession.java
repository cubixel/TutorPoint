package application.controller.services;

/**
 * CLASS DESCRIPTION: This class is used to package a text chat session request.
 *
 * @author Oli Clarke
 */
public class TextChatRequestSession {

  private Integer userID;
  private Integer sessionID;

  public TextChatRequestSession(Integer userID, Integer sessionID) {
    this.userID = userID;
    this.sessionID = sessionID;
  }

}
