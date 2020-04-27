package application.model;

public class Message {

  private Integer userID;
  private Integer sessionID;
  private String msg;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param userID          Message sent userID.
   * @param sessionID       Session to which the message belongs to.
   * @param msg             Message body text.
   */

  public Message(Integer userID, Integer sessionID, String msg) {
    this.userID = userID;
    this.sessionID = sessionID;
    this.msg = msg;
  }

  public Message() {

  }

  public Integer getUserID() {
    return userID;
  }

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public Integer getSessionID() {
    return sessionID;
  }

  public void setSessionID(Integer sessionID) {
    this.sessionID = sessionID;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}