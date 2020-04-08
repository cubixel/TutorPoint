/*
 * ViewFactory.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * CLASS DESCRIPTION:
 * This class is testing the Account class. It tests both
 * constructors that can create and Account object and
 * confirms the fields within the Account take the values
 * passed in.
 *
 * @author CUBIXEL
 *
 */
public class AccountTest {

  private Account account;
  private String someUsername = "someUsername";
  private String somePassword = "somePassword";

  @Test
  public void testFieldsBasicConstructor() {
    account = new Account(someUsername, somePassword);
    assertEquals(account.getUsername(), someUsername);
    assertEquals(account.getHashedpw(), somePassword);
  }

  /**
   * METHOD DESCRIPTION.
   */
  @Test
    public void testFieldsLargerConstructor() {
    int userID = 0;
    int tutorStatus = 1;
    int isRegister = 0;
    String someEmailAddress = "someEmail";

    account = new Account(userID, someUsername, someEmailAddress, somePassword, tutorStatus, isRegister);

    assertEquals(someUsername, account.getUsername());
    assertEquals(somePassword, account.getHashedpw());
    assertEquals(someEmailAddress, account.getEmailAddress());
    assertEquals(tutorStatus, account.getTutorStatus());
    assertEquals(isRegister, account.getIsRegister());
    assertEquals(userID, account.getUserID());

  }

}
