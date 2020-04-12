package application.controller.services;

public class WhiteboardRequestSession {

//  private MainConnection connection;
  private String userID;
  private String sessionID;
  private boolean tutorOnlyAccess;

  public WhiteboardRequestSession(String userID, String sessionID) {
//    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
    this.tutorOnlyAccess = true;
  }

//  public WhiteboardRequestSession(MainConnection mainConnection, String userID) {
//    this.connection = mainConnection;
//    this.userID = userID;
//    this.tutorOnlyAccess = true;
//  }
}
