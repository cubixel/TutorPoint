package application.controller.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is testing the Security Abstract class and
 * its methods. A JavaFX toolkit must be initialised
 * as the methods interact with a JavaFX Label.
 */
public class SecurityTest {

  private Label errorLabel;

  /**
   * The JavaFX Toolkit is initialised.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialised ..."));
  }


  /**
   * Instantiated the Classes need for the tests.
   */
  @BeforeEach
  public void setUp() {
    errorLabel = new Label();
  }


  @Test
  public void hashTest() {
    String testString = "thisisatest";
    String hashedString = "51bdbbadebdd4d81ea94f84f99f326f52091a918fc39d570e4e1d8d34a46ec42";
    assertEquals(hashedString, Security.hashPassword(testString));
  }

  @Test
  public void usernameIsValidTest() {
    String username;

    username = "";
    assertEquals(false, Security.usernameIsValid(username, errorLabel));
    assertEquals("Please Enter Username", errorLabel.getText());

    username = "usernamethatistoolong";
    assertEquals(false, Security.usernameIsValid(username, errorLabel));
    assertEquals("Username Too Long", errorLabel.getText());

    username = "tp";
    assertEquals(false, Security.usernameIsValid(username, errorLabel));
    assertEquals("Username Too Short", errorLabel.getText());

    username = "no whitespace";
    assertEquals(false, Security.usernameIsValid(username, errorLabel));
    assertEquals("Username should only contains letters and have no spaces",
        errorLabel.getText());

    username = "nonumb3rs";
    assertEquals(false, Security.usernameIsValid(username, errorLabel));
    assertEquals("Username should only contains letters and have no spaces",
        errorLabel.getText());

    username = "ValidUsername";
    assertEquals(true, Security.usernameIsValid(username, errorLabel));
  }

  @Test
  public void emailIsValid() {
    String[] email = {
        "",
        "email@test.com",

        // Invalid Emails
        ".username@yahoo.com",
        "username@yahoo.com.",
        "username@yahoo..com",
        "username@yahoo.c",
        "username@yahoo.corporate",

        // Valid Emails
        "user@domain.com",
        "user@domain.co.in",
        "user.name@domain.com",
        "user_name@domain.com",
        "username@yahoo.corporate.in"};

    String[] confirmEmail = {
        "",
        "emaildifferent@test.com",

        // Invalid Emails
        ".username@yahoo.com",
        "username@yahoo.com.",
        "username@yahoo..com",
        "username@yahoo.c",
        "username@yahoo.corporate",

        // Valid Emails
        "user@domain.com",
        "user@domain.co.in",
        "user.name@domain.com",
        "user_name@domain.com",
        "username@yahoo.corporate.in"};

    assertEquals(false, Security.emailIsValid(email[0], confirmEmail[0], errorLabel));
    assertEquals("Please Enter Email", errorLabel.getText());

    assertEquals(false, Security.emailIsValid(email[1], confirmEmail[1], errorLabel));
    assertEquals("Emails Don't Match", errorLabel.getText());

    assertEquals(false, Security.emailIsValid(email[2], confirmEmail[2], errorLabel));
    assertEquals("Email Not Valid", errorLabel.getText());
    assertEquals(false, Security.emailIsValid(email[3], confirmEmail[3], errorLabel));
    assertEquals("Email Not Valid", errorLabel.getText());
    assertEquals(false, Security.emailIsValid(email[4], confirmEmail[4], errorLabel));
    assertEquals("Email Not Valid", errorLabel.getText());
    assertEquals(false, Security.emailIsValid(email[5], confirmEmail[5], errorLabel));
    assertEquals("Email Not Valid", errorLabel.getText());
    assertEquals(false, Security.emailIsValid(email[6], confirmEmail[6], errorLabel));
    assertEquals("Email Not Valid", errorLabel.getText());

    assertEquals(true, Security.emailIsValid(email[7], confirmEmail[7], errorLabel));
    assertEquals(true, Security.emailIsValid(email[8], confirmEmail[8], errorLabel));
    assertEquals(true, Security.emailIsValid(email[9], confirmEmail[9], errorLabel));
    assertEquals(true, Security.emailIsValid(email[10], confirmEmail[10], errorLabel));
    assertEquals(true, Security.emailIsValid(email[11], confirmEmail[11], errorLabel));
  }

  @Test
  public void passwordIsValid() {
    String[] password = {
        "",
        "PasW0rd_isVal1d",
        "PAssW0_rd With WhiteSpace",
        "PasswordMissingNumber!",
        "passw0rd_missing_captials",
        "Passw0rdMissingSymbol",
        "'?dW{d9",

        // Valid Passwords
        "h!yu7j)DdHd9XiC",
        "97rx:g'6X9{6982",
        "?gRwyp+W.yV}BV4",
        "DdwAi8/6A[7m_37",
        "S9M!48Nt38x5["};

    String[] confirmPassword = {
        "",
        "PasW0rd_isDifferent",
        "PAssW0_rd With WhiteSpace",
        "PasswordMissingNumber!",
        "passw0rd_missing_captials",
        "Passw0rdMissingSymbol",
        "'?dW{d9",

        // Valid Passwords
        "h!yu7j)DdHd9XiC",
        "97rx:g'6X9{6982",
        "?gRwyp+W.yV}BV4",
        "DdwAi8/6A[7m_37",
        "S9M!48Nt38x5["};

    assertFalse(Security.passwordIsValid(password[0], confirmPassword[0], errorLabel));
    assertEquals("Please Enter Password", errorLabel.getText());

    assertFalse(Security.passwordIsValid(password[1], confirmPassword[1], errorLabel));
    assertEquals("Passwords Don't Match", errorLabel.getText());

    assertFalse(Security.passwordIsValid(password[2], confirmPassword[2], errorLabel));
    assertEquals("Use 8 or more characters with a mix of letters,\nnumbers & symbols",
        errorLabel.getText());
    assertFalse(Security.passwordIsValid(password[3], confirmPassword[3], errorLabel));
    assertEquals("Use 8 or more characters with a mix of letters,\nnumbers & symbols",
        errorLabel.getText());
    assertFalse(Security.passwordIsValid(password[4], confirmPassword[4], errorLabel));
    assertEquals("Use 8 or more characters with a mix of letters,\nnumbers & symbols",
        errorLabel.getText());
    assertFalse(Security.passwordIsValid(password[5], confirmPassword[5], errorLabel));
    assertEquals("Use 8 or more characters with a mix of letters,\nnumbers & symbols",
        errorLabel.getText());
    assertFalse(Security.passwordIsValid(password[6], confirmPassword[6], errorLabel));
    assertEquals("Use 8 or more characters with a mix of letters,\nnumbers & symbols",
        errorLabel.getText());

    assertTrue(Security.passwordIsValid(password[7], confirmPassword[7], errorLabel));
    assertTrue(Security.passwordIsValid(password[8], confirmPassword[8], errorLabel));
    assertTrue(Security.passwordIsValid(password[9], confirmPassword[9], errorLabel));
    assertTrue(Security.passwordIsValid(password[10], confirmPassword[10], errorLabel));
    assertTrue(Security.passwordIsValid(password[11], confirmPassword[11], errorLabel));

  }
}
