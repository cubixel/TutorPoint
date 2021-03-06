package application.model;

/**
 * CLASS DESCRIPTION: Message model used to send messages of text between users of a teaching
 * session.
 *
 * @author Oli Clarke
 */
public class Message {

  private Integer userID;       // Client User ID.
  private String userName;      // CLient Username.
  private Integer sessionID;    // Session ID for text chat.
  private String msg;           // Message body text.


  /**
   * Main class constructor.
   */
  public Message(String userName, Integer userID, Integer sessionID, String msg) {
    this.userID = userID;
    this.userName = userName;
    this.sessionID = sessionID;
    this.msg = msg;
  }

  // TODO, try run and delete this.
  public Message() {

  }

  /**
   * GETTERS & SETTERS.
   **/

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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}