package application.model.requests;

/**
 * This Class is used to contain the information used for
 * starting or joining a session. It is packaged as a Json
 * and sent to the server.
 *
 * @author Che McKirgan
 * @author James Gardner
 * @see application.controller.services.SessionRequestService
 * @see application.controller.StreamWindowController
 */
public class SessionRequest {

  int userID;
  int sessionID;
  boolean isHost;
  boolean leavingSession;

  /**
   * Constructor for the SessionRequest.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param sessionID
   *        A unique ID so that others can join the session if the User is
   *        the host of the session this should just be the same as the userID
   *
   * @param isHost
   *        Is the user requesting to start a session as a host or join another users
   *        session
   *
   * @param leavingSession
   *        Is this SessionRequest joining or leaving a session
   */
  public SessionRequest(int userID, int sessionID, Boolean isHost, Boolean leavingSession) {
    this.userID = userID;
    this.sessionID = sessionID;
    this.isHost = isHost;
    this.leavingSession = leavingSession;
  }
}