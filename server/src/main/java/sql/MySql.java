package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION.
 * 
 * @author CUBIXEL
 */
public class MySql {
  // TODO: Add enum for MySQL exceptions/failures.
  private String databaseName;
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSetUsername = null;
  private ResultSet resultSetEmail = null;
  private static final Logger log = LoggerFactory.getLogger("Server Logger");

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
      log.info("MySql: Successfully connected to database, " + databaseName);
    } catch (ClassNotFoundException cnfe) {
      log.error("MySql: Error while connecting to MySQL Database", cnfe);
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
      preparedStatement.setInt(4, tutorStatus);
      preparedStatement.executeUpdate();
      return usernameExists(username);
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * /
   * @param userID
   * @param username
   */
  public void removeAccount(int userID, String username) {
    try {
      String state = "DELETE FROM " + databaseName + ".users WHERE BINARY userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
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
    String state = "SELECT * FROM " + databaseName + ".subjects WHERE subjectID = ?";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, currentNumberSent + 1);
    return preparedStatement.executeQuery();
  }

  public boolean addSubject(Subject subject) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".subjects (subjectname, thumbnailpath, filename) "
          + "VALUES (?,?,?)";
      //statement.executeUpdate();
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, subject.getName());
      preparedStatement.setString(2, subject.getThumbnailPath());
      preparedStatement.setString(3, subject.getNameOfThumbnailFile());
      preparedStatement.executeUpdate();
      return subjectExists(subject.getName());
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * Takes a username and sends a query to the DB to check if
   * the user exists, if this is the case the user details is
   * returned from the server.
   * @param  subjectName Identifier of the user as received from the client
   */
  public boolean subjectExists(String subjectName) {
    try {
      String state = "SELECT * FROM " + databaseName + ".subjects WHERE BINARY subjectname = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, subjectName);
      ResultSet resultSetSubject = preparedStatement.executeQuery();
      return resultSetSubject.next();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }


  public void updateDetails(int userID, String usernameUpdate, String emailAddressUpdate,
      String hashedpwUpdate, int tutorStatusUpdate) {
    String state;
    try {
      if (!emailAddressUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET email = ? WHERE userID = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, emailAddressUpdate);
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
      }

      if (!hashedpwUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET hashedpw = ? WHERE userID = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, hashedpwUpdate);
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
      }

      if (tutorStatusUpdate != -1) {
        state = "UPDATE " + databaseName + ".users SET istutor = ? WHERE userID = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, String.valueOf(tutorStatusUpdate));
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
      }

      if (!usernameUpdate.equals("null")) {
        state = "UPDATE " + databaseName + ".users SET username = ? WHERE userID = ?";
        preparedStatement = connect.prepareStatement(state);
        preparedStatement.setString(1, usernameUpdate);
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public String getEmailAddress(int userID) {
    try {
      String state = "SELECT email FROM " + databaseName + ".users WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      resultSetEmail = preparedStatement.executeQuery();
      resultSetEmail.next();
      return resultSetEmail.getString("email");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  public int getUserID(String username) {
    try {
      String state = "SELECT userID FROM " + databaseName + ".users WHERE username = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, username);
      ResultSet resultSetUserID = preparedStatement.executeQuery();
      resultSetUserID.next();
      return resultSetUserID.getInt("userID");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return -1;
    }
  }

  public int getTutorStatus(int userID) {
    try {

      String state = "SELECT istutor FROM " + databaseName + ".users WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      ResultSet resultSetTutorStatus = preparedStatement.executeQuery();
      resultSetTutorStatus.next();
      return resultSetTutorStatus.getInt("istutor");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return -1;
    }
  }

  public String getUsername(int userID) {
    try {
      String state = "SELECT username FROM " + databaseName + ".users WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      ResultSet resultSetUsername = preparedStatement.executeQuery();
      resultSetUsername.next();
      return resultSetUsername.getString("username");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  public ResultSet getFollowedTutors(int userID) {
    try {
      String state = "SELECT tutorID FROM " + databaseName + ".followedtutors WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      return preparedStatement.executeQuery();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  public void addToFollowedTutors(int userID, int tutorID) {
    try {
      String state = "INSERT INTO " + databaseName + ".followedtutors (userID, tutorID) "
          + "VALUES (?,?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.setInt(2, tutorID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public void removeFromFollowedTutors(int userID, int tutorID) {
    try {
      String state = "DELETE FROM " + databaseName + ".followedtutors "
          + "WHERE (userID, tutorID) VALUES (?,?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.setInt(2, tutorID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  private void setTutorIsLive(int userID) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".livetutors (userID) "
          + "VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public ResultSet getLiveTutors(int tutorID) {
    // TODO Returns the list of tutors that are online that are also in the followed table
    try {
      String state = "SELECT tutorID "
          + "FROM " + databaseName + ".livetutors "
          + "INNER JOIN followedtutors ON livetutors.userID = followedtutors.tutorID "
          + "WHERE userID = ?";

      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, tutorID);
      return preparedStatement.executeQuery();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  private void setTutorNotLive(int tutorID) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "DELETE FROM " + databaseName + ".livetutors WHERE (userID) VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, tutorID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public void setLiveSession(int sessionID, int tutorID, String sessionName, String thumbnailPath) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".livesessions (sessionID, tutorID, "
          + "sessionname, thumbnailpath) "
          + "VALUES (?,?,?,?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, sessionID);
      preparedStatement.setInt(2, tutorID);
      preparedStatement.setString(3, sessionName);
      preparedStatement.setString(4, thumbnailPath);
      preparedStatement.executeUpdate();
      setTutorIsLive(tutorID);
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public void endLiveSession(int sessionID, int tutorID) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "DELETE FROM " + databaseName + ".livesessions WHERE (sessionID) VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, sessionID);
      preparedStatement.executeUpdate();
      setTutorNotLive(tutorID);
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }
}
