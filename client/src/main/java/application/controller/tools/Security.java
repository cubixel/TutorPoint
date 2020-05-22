package application.controller.tools;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Security contains a set of Static methods used throughout the
 * program to improve security. This includes hashing passwords
 * but also checking usernames, passwords and emails provided by
 * the user meet TutorPoint standards.
 *
 * @author Daniel Bishop
 * @author James Gardner
 * @see    DigestUtils
 * @see    Pattern
 * @see    Matcher
 */
public class Security {

  /**
   * Takes a String and returns the sha3_256Hex representation
   * of that String.
   *
   * @param password
   *        A String containing the password to be hashed
   *
   * @return A hashed representation of the provided String using
   *         DigestUtils.sha3_256Hex()
   */
  public static String hashPassword(String password) {

    // TODO: Add salt to password before hashing.
    // *** NOTE ***
    // - Password should add salt to minimise collisions or remove identical
    //   hashes on commonly used passwords.
    // - Salt must be a unique fixed length string of random characters and
    //   stored along side the user's data on the server database.
    //      - Could use the user's ID if uID is randomly assigned.
    // - Salt is appended to the start of the password BEFORE the hashing algorithm.
    // Author: OS [21:00 15/02/2020]

    return DigestUtils.sha3_256Hex(password);
  }

  /**
   * This checks whether a provided String matches the username standards
   * TutorPoint uses. A username should only contains Letters, must be less
   * than 20 characters but more than 4 and can't contain whitespaces.
   *
   * @param username
   *        The String containing the username to check
   *
   * @param errorLabel
   *        A JavaFX Label to write an error message too
   *
   * @return {@code false} if the username is empty, greater than 20, less than 4
   *         contains special characters, numbers or whitespace. Otherwise {@code true}.
   */
  public static Boolean usernameIsValid(String username, Label errorLabel) {
    

    if (username.isEmpty()) {
      errorLabel.setText("Please Enter Username");
      return false;
    }

    if (username.length() > 20) {
      errorLabel.setText("Username Too Long");
      return false;
    }

    if (username.length() < 4) {
      errorLabel.setText("Username Too Short");
      return false;
    }

    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    Pattern whiteSpace = Pattern.compile("[\\s]");

    if (specialCharPatten.matcher(username).find() || whiteSpace.matcher(username).find()
        || digitCasePatten.matcher(username).find()) {
      errorLabel.setText("Username should only contains letters and have no spaces");
      return false;
    }
    return true;
  }

  /**
   * This checks whether a provided String matches the email regex standards
   * TutorPoint uses. Also checks that the two emails provided are equal.
   *
   * @param email
   *        The String containing the email to check
   *
   * @param confirmEmail
   *        The String containing a repeat of the email to compare 'email' with
   *
   * @param errorLabel
   *        A JavaFX Label to write an error message too
   *
   * @return {@code false} if the email is empty, does not match confirmEmail, or
   *         fail to match the regex. Otherwise {@code true}.
   */
  public static Boolean emailIsValid(String email, String confirmEmail, Label errorLabel) {

    if (email.isEmpty()) {
      errorLabel.setText("Please Enter Email");
      return false;
    }

    if (!(Objects.equals(email, confirmEmail))) {
      errorLabel.setText("Emails Don't Match");
      return false;
    }

    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`"
        + "{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    if (!matcher.matches()) {
      errorLabel.setText("Email Not Valid");
      return false;
    }
    return true;
  }

  /**
   * This checks whether a provided String matches the password standards
   * TutorPoint uses. It must be at least eight characters long with a
   * mix of letters number and symbols. Checks that the two passwords
   * provided match.
   *
   * @param password
   *        The String containing the password to check
   *
   * @param confirmPassword
   *        The String containing a repeat of the password to compare 'password' with
   *
   * @param errorLabel
   *        A JavaFX Label to write an error message too
   *
   * @return {@code false} if the password is empty, does not match confirmPassword, or
   *         fail to pass the regex tests. Otherwise {@code true}.
   */
  public static boolean passwordIsValid(String password, String confirmPassword, Label errorLabel) {

    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern upperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    Pattern whiteSpace = Pattern.compile("[\\s]");

    if (password.isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      errorLabel.setText("Passwords Don't Match");
      return false;
    }

    if (!specialCharPatten.matcher(password).find() || !upperCasePatten.matcher(password).find()
        || !lowerCasePatten.matcher(password).find() || !digitCasePatten.matcher(password).find()
        || password.length() < 8 || whiteSpace.matcher(password).find()) {
      errorLabel.setText("Use 8 or more characters with mix-case letters,\nnumbers & symbols");
      return false;
    }
    return true;
  }
}
