package application.model;

public class Message {

  private String userID;
  private String sessionID;
  private String msg;


  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param userID          Message sent userID.
   * @param sessionID       Session to which the message belongs to.
   * @param msg             Message body text.
   */

  public Message(String userID, String sessionID, String msg) {
    this.userID = userID;
    this.sessionID = sessionID;
    this.msg = msg;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}