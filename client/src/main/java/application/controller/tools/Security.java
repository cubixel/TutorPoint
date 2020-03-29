/*
 * Security.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.controller.tools;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class Security {

  /**
   * METHOD DESCRIPTION.
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

    // Hash password using the SHA3_256 algorithm.
    String hash = DigestUtils.sha3_256Hex(password);
    return hash;
  }


  public static Boolean usernameIsValid(String username, Label errorLabel) {
    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    Pattern whiteSpace = Pattern.compile("[\\s]");

    if (username.isEmpty()) {
      errorLabel.setText("Please Enter Username");
      return false;
    }

    if (username.length() > 20) {
      errorLabel.setText("Username Too Long");
      return false;
    }

    if (specialCharPatten.matcher(username).find() || whiteSpace.matcher(username).find()
        || digitCasePatten.matcher(username).find()) {
      errorLabel.setText("Username should only contains letters and have no spaces");
      return false;
    }
    return true;
  }

  public static Boolean emailIsValid(String email, String confirmEmail, Label errorLabel) {

    if (email.isEmpty()) {
      errorLabel.setText("Please Enter Email");
      return false;
    }

    if (!(Objects.equals(email, confirmEmail))) {
      errorLabel.setText("Emails Don't Match");
      return false;
    }

    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    if (!matcher.matches()) {
      errorLabel.setText("Email Not Valid");
      return false;
    }
    return true;
  }

  public static boolean passwordIsValid(String password, String confirm, Label errorLabel) {

    Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern upperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");

    if (password.isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }

    if (!password.equals(confirm)) {
      errorLabel.setText("Passwords Don't Match");
      return false;
    }

    if (!specialCharPatten.matcher(password).find() || !upperCasePatten.matcher(password).find()
        || !lowerCasePatten.matcher(password).find() || !digitCasePatten.matcher(password).find()
        || password.length() < 8) {
      errorLabel.setText("Use 8 or more characters with a mix of letters,\nnumbers & symbols");
      return false;
    }
    return true;
  }
}
