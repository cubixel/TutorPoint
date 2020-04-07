package model;


import java.util.ArrayList;
import java.util.List;

public class Account {

  private int userID;
  private String username;
  private String emailAddress;
  private String hashedpw;
  private int tutorStatus = 0;
  private int isRegister = 0;
  private float rating;
  private List<String> followedSubjects = new ArrayList<>();

  /**
   * This is an Account Class. It contains all the information on a User.
   *
   * @param username     A unique string to identify the user.
   * @param emailAddress A string of an email address.
   * @param hashedpw     A string with the hashed password.
   * @param tutorStatus  1 for Tutor, 0 for Not a Tutor.
   * @param isRegister   1 means this Account needs creating on the server side.
   */
  public Account(int userID, String username, String emailAddress, String hashedpw,
      int tutorStatus, int isRegister) {
    this.userID = userID;
    this.username = username;
    this.emailAddress = emailAddress;
    this.hashedpw = hashedpw;
    this.tutorStatus = tutorStatus;
    this.isRegister = isRegister;

    // TODO: Add user ID/salt for password hashing.
  }

  public Account(String username, String emailAddress, String hashedpw,
      int tutorStatus, int isRegister) {
    this.username = username;
    this.emailAddress = emailAddress;
    this.hashedpw = hashedpw;
    this.tutorStatus = tutorStatus;
    this.isRegister = isRegister;

    // TODO: Add user ID/salt for password hashing.
  }


  public Account(String username, String hashedpw) {
    this.username = username;
    this.hashedpw = hashedpw;
  }

  public Account(String username, int userID, float rating) {
    this.username = username;
    this.userID = userID;
    this.rating = rating;
  }

  public int getUserID() {
    return userID;
  }

  public String getUsername() {
    return username;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getHashedpw() {
    return hashedpw;
  }

  public int getTutorStatus() {
    return tutorStatus;
  }

  public int getIsRegister() {
    return isRegister;
  }

  public float getRating() {
    return rating;
  }

  public List<String> getFollowedSubjects() {
    return followedSubjects;
  }

  public void setFollowedSubjects(List<String> followedSubjects) {
    this.followedSubjects = followedSubjects;
  }

  public void addFollowedSubjects(String subject) {
    this.followedSubjects.add(subject);
  }

  public void setTutorStatus(int tutorStatus) {
    this.tutorStatus = tutorStatus;
  }

  public void setHashedpw(String hashedpw) {
    this.hashedpw = hashedpw;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }
}
