/*
 * MySql.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION.
 * 
 * @author CUBIXEL
 */
public class MySql {
  // TODO: Add enum for MySQL exceptions/failures.
  private String databaseName = null;
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSetUsername = null;
  private ResultSet resultSetEmail = null;
  private static final Logger log = LoggerFactory.getLogger("MySql");

  /**
   * .
   * @param databaseName
   */
  public MySql(String databaseName) throws SQLException {
    this.databaseName = databaseName;
    try {
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.cj.jdbc.Driver");
      // Setup the connection with the DB
      connect = DriverManager.getConnection("jdbc:mysql://cubixel.ddns.net:52673/" + databaseName
          + "?" + "user=java&password=2pWwoP6EBH5U7XpoYuKd");
      log.info("Successfully connected to database: " + databaseName);
    } catch (ClassNotFoundException cnfe) {
      log.error("Error while connecting to MySQL Database", cnfe);
    }
  }

  /**
   * Takes a username and sends a query to the DB to check if
   * the user exists, if this is the case the user details is
   * returned from the server.
   * @param  username Identifier of the user as received from the client
   */
  public boolean usernameExists(String username) {
    try {
      String state = "SELECT * FROM " + databaseName + ".users WHERE BINARY username = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, username);
      resultSetUsername = preparedStatement.executeQuery();
      return resultSetUsername.next();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * .
   * @param  email .
   */
  public boolean emailExists(String email) {
    try {
      String state = "SELECT * FROM " + databaseName + ".users WHERE BINARY email = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, email);
      resultSetEmail = preparedStatement.executeQuery();
      return resultSetEmail.next();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }


  /**
   * METHOD DESCRIPTION.
   */
  public boolean checkUserDetails(String username, String hashedpw) {
    try {
      // HashedPW isn't direct user input so prepared statement not needed.
      if (usernameExists(username)) {
        statement = connect.createStatement();
        resultSetUsername = statement.executeQuery("SELECT * FROM  " + databaseName
            + ".users WHERE BINARY hashedpw = '" + hashedpw + "'");
        return resultSetUsername.next();
      } else {
        return false;
      }
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public boolean createAccount(String username, String email, String hashpw, int tutorStatus) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".users (username, email, hashedpw, istutor) "
          + "VALUES (?,?,?,?)";
      //statement.executeUpdate();
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, email);
      preparedStatement.setString(3, hashpw);
      preparedStatement.setString(4, String.valueOf(tutorStatus));
      preparedStatement.executeUpdate();
      return usernameExists(username);
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * METHOD DESCRIPTION.
   * 
   * @param username DESCRIPTION
   */
  public void removeAccount(String username) {
    try {
      String state = "DELETE FROM " + databaseName + ".users WHERE BINARY username = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, username);
      preparedStatement.executeUpdate();
      log.info("Account: " + username + "Successfully Removed");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public ResultSet getNextSubjects(int currentNumberSent) throws SQLException {
    statement = connect.createStatement();
    resultSetUsername = statement.executeQuery("SELECT * FROM  " + databaseName
        + ".subjects WHERE id = '" + (currentNumberSent + 1) + "'");
    return resultSetUsername;
  }

  public void updateDetails(String username, String usernameUpdate, String emailAddressUpdate,
      String hashedpwUpdate, int tutorStatusUpdate) {
    String state;
    try {
      if (!emailAddressUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET email = ? WHERE username = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, emailAddressUpdate);
        preparedStatement.setString(2, username);
        preparedStatement.executeUpdate();
      }

      if (!hashedpwUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET hashedpw = ? WHERE username = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, hashedpwUpdate);
        preparedStatement.setString(2, username);
        preparedStatement.executeUpdate();
      }

      if (tutorStatusUpdate != -1) {
        state = "UPDATE " + databaseName + ".users SET istutor = ? WHERE username = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, String.valueOf(tutorStatusUpdate));
        preparedStatement.setString(2, username);
        preparedStatement.executeUpdate();
      }

      if (!usernameUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET username = ? WHERE username = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, usernameUpdate);
        preparedStatement.setString(2, username);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public String getEmailAddress(String username) {
    try {
      resultSetEmail = statement.executeQuery("SELECT email FROM  " + databaseName
          + ".users WHERE username = '" + username + "'");
      resultSetEmail.next();
      return resultSetEmail.getString("email");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  public int getTutorStatus(String username) {
    try {
      resultSetEmail = statement.executeQuery("SELECT istutor FROM  " + databaseName
          + ".users WHERE username = '" + username + "'");
      resultSetEmail.next();
      return Integer.parseInt(resultSetEmail.getString("istutor"));
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return -1;
    }
  }
}
