package application.model;

// This will be the basic user class.
public class Account {
  String username;
  String emailAddress;
  String hashedpw;
  int tutorStatus = 0;
  int isRegister = 0;

  /**
   * CONSTRUCTOR DESCRIPTION.
   * 
   * @param username      DESCRIPTION
   * @param emailAddress  DESCRIPTION
   * @param hashedpw      DESCRIPTION
   * @param tutorStatus   DESCRIPTION
   * @param isRegister    DESCRIPTION
   */
  public Account(String username, String emailAddress, String hashedpw, int tutorStatus,
      int isRegister) {
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

  public void setTutorStatus(int tutorStatus) {
    this.tutorStatus = tutorStatus;
  }

  public void setHashedpw(String hashedpw) {
    this.hashedpw = hashedpw;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
