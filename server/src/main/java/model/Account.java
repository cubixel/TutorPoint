package model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 * The account class contains all user information. It is used
 * in both registering and logging in an account. An Account
 * is used for both the users profile but also that of other
 * Account displayed within the app such as on the Tutor
 * profile pages.
 *
 * @author James Gardner
 */
public class Account {

  private int userID = -1;
  private String username;
  private String emailAddress = "";
  private String hashedpw = "";
  private int tutorStatus = 0;
  private int isRegister = 0;
  private float rating;
  private List<String> followedSubjects = new ArrayList<>();
  private Image profilePicture = null;

  /**
   * Most detailed class constructor used to build an
   * Account when all information is known.
   *
   * @param userID
   *        A unique integer to identify the user provided by the database
   *
   * @param username
   *        A unique string to identify the user
   *
   * @param emailAddress
   *        A string of an email address
   *
   * @param hashedpw
   *        A string with the hashed password
   *
   * @param tutorStatus
   *        1 for Tutor, 0 for Not a Tutor
   *
   * @param isRegister
   *        1 means this Account needs creating on the server side
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

  /**
   * Partially detailed class constructor used to build an
   * Account when all information is known except the
   * userID which is provided by the server.
   *
   * @param username
   *        A unique string to identify the user
   *
   * @param emailAddress
   *        A string of an email address
   *
   * @param hashedpw
   *        A string with the hashed password
   *
   * @param tutorStatus
   *        1 for Tutor, 0 for Not a Tutor
   *
   * @param isRegister
   *        1 means this Account needs creating on the server side
   */
  public Account(String username, String emailAddress, String hashedpw,
      int tutorStatus, int isRegister) {
    this.username = username;
    this.emailAddress = emailAddress;
    this.hashedpw = hashedpw;
    this.tutorStatus = tutorStatus;
    this.isRegister = isRegister;

    // TODO: Add user ID/salt for password hashing.
  }


  /**
   * Used on initial login constructor used
   * when no other data is yet known.
   *
   * @param username
   *        A unique string to identify the user
   *
   * @param hashedpw
   *        A string with the hashed password
   */
  public Account(String username, String hashedpw) {
    this.username = username;
    this.hashedpw = hashedpw;
  }

  /**
   * Used as the public profile of other accounts.
   *
   * @param username
   *        A unique string to identify the user
   *
   * @param userID
   *        A unique integer to identify the user provided by the database
   *
   * @param rating
   *        The rating on the database that this account has
   */
  public Account(String username, int userID, float rating) {
    this.username = username;
    this.userID = userID;
    this.rating = rating;
  }

  public Image getProfilePicture() {
    return profilePicture;
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

  public void setProfilePicture(Image profilePicture) {
    this.profilePicture = profilePicture;
  }
}
