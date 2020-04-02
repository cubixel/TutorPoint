package application.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Message {

  private String userID;
  private int sessionID;
  private String msg;
  private ArrayList<Account> users;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param userID          Message sent userID.
   * @param sessionID       Session to which the message belongs to.
   * @param msg             Message body text.
   * /* @param users        List of all users in current session chat.  (Might implement later, thinking user list
   *                                                                    in message but can't get JSon to work)
   */

  public Message(String userID, Integer sessionID, String msg) {
    this.userID = userID;
    this.sessionID = sessionID ;
    this.msg = msg;
    // this.users = users;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public int getSessionID() {
    return sessionID;
  }

  public void setSessionID(int sessionID) {
    this.sessionID = sessionID;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  // User list in message bits

  public ArrayList<Account> getUserlist() {
    return users;
  }

  public ArrayList<Account> getUsers() {
    return users;
  }

  public void setUserlist(HashMap<String, Account> userList) {
    this.users = new ArrayList<>(userList.values());
  }

  public int getOnlineCount(){
    return this.users.size();
  }

  public void setUsers(ArrayList<Account> users) {
    this.users = users;
  }
}