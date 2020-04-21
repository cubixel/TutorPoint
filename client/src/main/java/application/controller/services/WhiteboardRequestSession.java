package application.controller.services;

/**
 * This class is used to package the query information
 * about the session.
 *
 * @author Che McKirgan
 */
public class WhiteboardRequestSession {

  private String userID;
  private String sessionID;

  public WhiteboardRequestSession(String userID, String sessionID) {
    this.userID = userID;
    this.sessionID = sessionID;
  }
}
