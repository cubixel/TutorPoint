package application.controller.services;

public class TextChatRequestSession {

    private String userID;
    private String sessionID;

    public TextChatRequestSession(String userID, String sessionID) {
      this.userID = userID;
      this.sessionID = sessionID;
    }

}
