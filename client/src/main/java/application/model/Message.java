package application.model;

import java.util.ArrayList;
import java.util.HashMap;
import application.controller.enums.TextRequestResult;
import application.model.Account;

public class Message {

  private String name;
  private TextRequestResult type;
  private String msg;
  private int onlineUserCount;
  private ArrayList<Account> list;
  private ArrayList<Account> users;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
    return list;
  }

  public void setUserlist(HashMap<String, Account> userList) {
    this.list = new ArrayList<>(userList.values());
  }

  public void setOnlineCount(int count){
    this.onlineUserCount = count;
  }

  public int getOnlineCount(){
    return this.onlineUserCount;
  }

  public ArrayList<Account> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<Account> users) {
    this.users = users;
  }
}