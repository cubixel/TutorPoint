/*
 * ViewFactory.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.model.Account;
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
    private String someEmailAddress = "someEmail";
    private String somePassword = "somePassword";

    @Test
    public void testFieldsBasicConstructor(){
        account = new Account(someUsername, somePassword);
        assertEquals(account.getUsername(), someUsername);
        assertEquals(account.getHashedpw(), somePassword);
    }

    @Test
    public void testFieldsLargerConstructor(){
        int tutorStatus = 1;
        int isRegister = 0;

        account = new Account(someUsername, somePassword, someEmailAddress, tutorStatus, isRegister);

        assertEquals(account.getUsername(), someUsername);
        assertEquals(account.getEmailAddress(), someEmailAddress);
        assertEquals(account.getHashedpw(), somePassword);
        assertEquals(account.getTutorStatus(), tutorStatus);
        assertEquals(account.getIsRegister(), isRegister);
    }

}