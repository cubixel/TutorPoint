package application.controller.services;

/**
 * This class is used to package the query information
 * about the session.
 *
 * @author Che McKirgan
 */
public class WhiteboardRequestSession {

  private int userID;
  private int sessionID;

  public WhiteboardRequestSession(int userID, int sessionID) {
    this.userID = userID;
    this.sessionID = sessionID;
    this.tutorOnlyAccess = true;
  }
}
