package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * This class is testing the Account class. It tests all
 * constructors that can create and Account object and
 * confirms the fields within the Account take the values
 * passed in and when using getters and setters.
 *
 * @author James Gardner
 */
public class AccountTest {

  private Account account;
  private final int userID = 1;
  private final String someUsername = "someUsername";
  private final String someEmail = "someemail@emailtest.com";
  private final String somePassword = "somePassword";
  private final int tutorStatus = 1;
  private final int isRegister = 0;

  @Test
  public void mostDetailedConstructorTest() {
    account = new Account(userID, someUsername, someEmail, somePassword, tutorStatus, isRegister);
    assertEquals(userID, account.getUserID());
    assertEquals(someUsername, account.getUsername());
    assertEquals(someEmail, account.getEmailAddress());
    assertEquals(somePassword, account.getHashedpw());
    assertEquals(tutorStatus, account.getTutorStatus());
    assertEquals(isRegister, account.getIsRegister());
  }

  @Test
  public void partiallyDetailedConstructorTest() {
    account = new Account(someUsername, someEmail, somePassword, tutorStatus, isRegister);
    assertEquals(-1, account.getUserID());
    assertEquals(someUsername, account.getUsername());
    assertEquals(someEmail, account.getEmailAddress());
    assertEquals(somePassword, account.getHashedpw());
    assertEquals(tutorStatus, account.getTutorStatus());
    assertEquals(isRegister, account.getIsRegister());
  }

  @Test
  public void loginConstructorTest() {
    account = new Account(someUsername, somePassword);
    assertEquals(-1, account.getUserID());
    assertEquals("", account.getEmailAddress());
    assertEquals(0, account.getTutorStatus());
    assertEquals(0, account.getIsRegister());

    assertEquals(someUsername, account.getUsername());
    assertEquals(somePassword, account.getHashedpw());
  }

  @Test
  public void otherUserConstructorTest() {
    int rating = 5;
    account = new Account(someUsername, userID, rating);
    assertEquals("", account.getEmailAddress());
    assertEquals("", account.getHashedpw());
    assertEquals(0, account.getTutorStatus());
    assertEquals(0, account.getIsRegister());

    assertEquals(someUsername, account.getUsername());
    assertEquals(userID, account.getUserID());
    assertEquals(rating, account.getRating());
  }

  @Test
  public void getterAndSetterTest() {
    account = new Account(userID, someUsername, someEmail, somePassword, tutorStatus, isRegister);
    int newUserID = 2;
    assertEquals(userID, account.getUserID());
    account.setUserID(newUserID);
    assertEquals(newUserID, account.getUserID());

    String newUsername = "newUsername";
    assertEquals(someUsername, account.getUsername());
    account.setUsername(newUsername);
    assertEquals(newUsername, account.getUsername());

    String newEmail = "newemail@emailtest.com";
    assertEquals(someEmail, account.getEmailAddress());
    account.setEmailAddress(newEmail);
    assertEquals(newEmail, account.getEmailAddress());

    String newPassword = "newPassword";
    assertEquals(somePassword, account.getHashedpw());
    account.setHashedpw(newPassword);
    assertEquals(newPassword, account.getHashedpw());

    int newTutorStatus = 0;
    assertEquals(tutorStatus, account.getTutorStatus());
    account.setTutorStatus(newTutorStatus);
    assertEquals(newTutorStatus, account.getTutorStatus());

    int newRating = 5;
    assertEquals(-1, account.getRating());
    account.setRating(newRating);
    assertEquals(newRating, account.getRating());
  }
}
