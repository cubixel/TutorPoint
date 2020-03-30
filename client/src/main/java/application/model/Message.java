package application.model;

import java.util.ArrayList;
import java.util.HashMap;
import application.controller.enums.TextRequestResult;

public class Message {

  private String userID;
  private TextRequestResult type;
  private String msg;
  private ArrayList<Account> users;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public TextRequestResult getType() {
    return type;
  }

  public void setType(TextRequestResult type) {
    this.type = type;
  }

  public ArrayList<Account> getUserlist() {
    return users;
  }

  public void setUserlist(HashMap<String, Account> userList) {
    this.users = new ArrayList<>(userList.values());
  }


  public int getOnlineCount(){
    return this.users.size();
  }

  public ArrayList<Account> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<Account> users) {
    this.users = users;
  }
}