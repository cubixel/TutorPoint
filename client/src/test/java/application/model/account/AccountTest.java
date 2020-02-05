/*
 * ViewFactory.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.model.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CLASS DESCRIPTION:
 * This class is testing the Account class. It tests both
 * constructors that can create and Account object and
 * confirms the fields within the Account take the values
 * passed in.
 *
 * @author James Gardner
 *
 */
public class AccountTest {

    private Account account;
    private String someUsername = "someUsername";
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

        account = new Account(someUsername, somePassword, tutorStatus, isRegister);

        assertEquals(account.getUsername(), someUsername);
        assertEquals(account.getHashedpw(), somePassword);
        assertEquals(account.getTutorStatus(), tutorStatus);
        assertEquals(account.getIsRegister(), isRegister);
    }

}
