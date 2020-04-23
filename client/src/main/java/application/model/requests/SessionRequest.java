package application.model.requests;

/**
 * This class is used to package the query information
 * about the session.
 *
 * @author Che McKirgan
 * @author James Gardner
 */
public class SessionRequest {

  private int userID;
  private int sessionID;
  private boolean isHost;
  private boolean leavingSession;

  public SessionRequest(int userID, int sessionID, Boolean isHost, Boolean leavingSession) {
    this.userID = userID;
    this.sessionID = sessionID;
    this.isHost = isHost;
    this.leavingSession = leavingSession;
  }
}
