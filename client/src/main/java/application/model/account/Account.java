package application.model.account;

// This will be the basic user class.
public class Account {
  String username;
  String hashedpw;
  int tutorStatus = 0;
  int isRegister = 0;

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public Account(String username, String hashedpw, int tutorStatus, int isRegister) {
    this.username = username;
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
}
