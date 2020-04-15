package application.controller.services;

/**
 * This class is used to request the session history
 * of a whiteboard session.
 *
 * @author Oliver Still
 */
public class WhiteboardHistoryRequest {
  String sessionID;
  String userID;

  /**
   * Main class constructor.
   *
   * @param sessionID Session ID of the stream.
   * @param userID User ID of the client.s
   */
  WhiteboardHistoryRequest(String sessionID, String userID) {
    this.sessionID = sessionID;
    this.userID = userID;
  }

  /* Setters and Getters */

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }
}
