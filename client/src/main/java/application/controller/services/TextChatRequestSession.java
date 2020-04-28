package application.controller.services;

public class TextChatRequestSession {

    private Integer userID;
    private Integer sessionID;

    public TextChatRequestSession(Integer userID, Integer sessionID) {
      this.userID = userID;
      this.sessionID = sessionID;
    }

}
