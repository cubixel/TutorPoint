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



  /**
   * Constructor that .....
   * // @param ## no parameters atm ##
   */
  public MySql(String databaseName) {
    this.databaseName = databaseName;
    try {
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.cj.jdbc.Driver");
      // Setup the connection with the DB
      connect = DriverManager.getConnection("jdbc:mysql://cubixel.ddns.net:52673/" + databaseName
          + "?" + "user=java&password=2pWwoP6EBH5U7XpoYuKd");
    } catch (ClassNotFoundException cnfe) {
        // TODO deal with error
    } catch (SQLException sqlE) {
        // TODO deal with error
    }
  }

  /**
   * Takes a username and sends a query to the DB to check if
   * the user exists, if this is the case the user details is
   * returned from the server.
   * @param  username Identifier of the user as received from the client
   */
  public boolean usernameExists(String username) {
    // TODO change to prepared statement
    try {
      String state = "SELECT * FROM " + databaseName + ".users WHERE BINARY username = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, username);
      resultSetUsername = preparedStatement.executeQuery();
      if (resultSetUsername.next()) {
        return true;
      } else {
        return false;
      }
    } catch (SQLException sqlE) {
      // TODO deal with error
      sqlE.printStackTrace();
      return false;
    }
  }

  /**
   * .
   * @param  email .
   */
  public boolean emailExists(String email) {
    // TODO change to prepared statement
    try {
      String state = "SELECT * FROM " + databaseName + ".users WHERE BINARY email = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, email);
      resultSetEmail = preparedStatement.executeQuery();
      return resultSetEmail.next();
    } catch (SQLException sqlE) {
      // TODO deal with error
      sqlE.printStackTrace();
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
        if (resultSetUsername.next()) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
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
    } catch (SQLException sqlE) {
      return false;
      // TODO deal with exception
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
    } catch (SQLException sqlE) {
        // TODO deal with exception
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public ResultSet getNextSubjects(int currentNumberSent) throws SQLException {
    statement = connect.createStatement();
    resultSetUsername = statement.executeQuery("SELECT * FROM  " + databaseName
        + ".subjects WHERE id = '" + (currentNumberSent + 1) + "'");
    System.out.println(resultSetUsername);
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
    } catch (SQLException sqlE) {
      sqlE.printStackTrace();
    }
  }

  public String getEmailAddress(String username) {
    try {
      resultSetEmail = statement.executeQuery("SELECT email FROM  " + databaseName
          + ".users WHERE username = '" + username + "'");
      resultSetEmail.next();
      return resultSetEmail.getString("email");
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int getTutorStatus(String username) {
    try {
      resultSetEmail = statement.executeQuery("SELECT istutor FROM  " + databaseName
          + ".users WHERE username = '" + username + "'");
      resultSetEmail.next();
      return Integer.parseInt(resultSetEmail.getString("istutor"));
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
