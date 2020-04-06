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
 * .
 *
 * @author Che McKirgan
 * @author James Gardner
 * @see    Statement
 * @see    PreparedStatement
 * @see    ResultSet
 * @see    services.ServerTools
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

  /* #####################################################################################
   * ########################### USER ACCOUNT RELATED METHODS ############################
   * #####################################################################################*/

  /**
   * METHOD DESCRIPTION.
   */
  public boolean createAccount(String username, String email, String hashpw, int tutorStatus) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".users (username, email, hashedpw, istutor) "
          + "VALUES (?,?,?,?)";
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

  /* #####################################################################################
   * ############################## SUBJECT RELATED METHODS ##############################
   * #####################################################################################*/


  public boolean addSubject(String name) {
    // TODO: Check docs for injection ability with these
    try {
      String state = "INSERT INTO " + databaseName + ".subjects (subjectname) "
          + "VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, name);
      preparedStatement.executeUpdate();
      return subjectExists(name);
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * /
   */
  public void removeSubject(int subjectID) {
    try {
      String state = "DELETE FROM " + databaseName + ".subjects WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.executeUpdate();

      state = "DELETE FROM " + databaseName + ".subjectRating WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.executeUpdate();
      log.info("Subject: " + subjectID + "Successfully Removed");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * METHOD DESCRIPTION
   */
  public ResultSet getSubjects() throws SQLException {
    String state = "SELECT * FROM " + databaseName + ".subjects";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  public int getSubjectID(String subjectName) {
    try {
      String state = "SELECT subjectID FROM " + databaseName + ".subjects WHERE subjectname = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, subjectName);
      ResultSet resultSetSubjectID = preparedStatement.executeQuery();
      resultSetSubjectID.next();
      return resultSetSubjectID.getInt("subjectID");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return -1;
    }
  }


  public void addSubjectRating(int subjectID, int userID, int rating) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".subjectrating (subjectID, userID, rating) "
        + "VALUES (?,?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, subjectID);
    preparedStatement.setInt(2, userID);
    preparedStatement.setInt(3, rating);
    preparedStatement.executeUpdate();
  }


  public int getUsersSubjectRating(int subjectID, int userID) throws SQLException {
    String state = "SELECT rating FROM " + databaseName + ".subjectrating WHERE"
        + " subjectID = ? AND userID = ?";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, subjectID);
    preparedStatement.setInt(2, userID);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    resultSetSubject.next();
    return resultSetSubject.getInt("rating");
  }

  /**
   * METHOD DESCRIPTION
   */
  public ResultSet getSubjectsDescendingByRating() throws SQLException {
    String state = "SELECT subjectID, avg(rating) AS rating FROM " + databaseName
        + ".subjectrating GROUP BY subjectID ORDER BY rating DESC";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  public float getAverageSubjectRating(int subjectID) throws SQLException {
    String state = "SELECT avg(rating) FROM " + databaseName + ".subjectrating WHERE"
        + " subjectID = '" + subjectID + "'";
    preparedStatement = connect.prepareStatement(state);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    resultSetSubject.next();
    return resultSetSubject.getFloat("avg(rating)");
  }

  public void removeSubjectRating(int subjectID, int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".subjectrating "
          + "WHERE subjectID = ? AND userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.setInt(2, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

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

  public void addSubjectCategory(int subjectID, int categoryID) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".subjectcategory (subjectID, categoryID) "
        + "VALUES (?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, subjectID);
    preparedStatement.setInt(2, categoryID);
    preparedStatement.executeUpdate();
  }

  /* #####################################################################################
   * ############################# CATEGORY RELATED METHODS ##############################
   * #####################################################################################*/

  public void addCategory(String categoryName) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".category (categoryname) "
        + "VALUES (?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setString(1, categoryName);
    preparedStatement.executeUpdate();
  }

  public int getCategoryID(String categoryName) {
    try {
      String state = "SELECT categoryID FROM " + databaseName + ".category WHERE categoryname = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, categoryName);
      ResultSet resultSetSubjectID = preparedStatement.executeQuery();
      resultSetSubjectID.next();
      return resultSetSubjectID.getInt("categoryID");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return -1;
    }
  }

  /* #####################################################################################
   * ############################### TUTOR RELATED METHODS ###############################
   * #####################################################################################*/

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

  public void addTutorRating(int tutorID, int userID, int rating) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".tutorrating (tutorID, userID, rating) "
        + "VALUES (?,?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, tutorID);
    preparedStatement.setInt(2, userID);
    preparedStatement.setInt(3, rating);
    preparedStatement.executeUpdate();
  }

  public void updateTutorRating(int tutorID, int userID, int rating) throws SQLException {
    String state = "UPDATE " + databaseName + ".tutorrating SET rating = ? WHERE tutorID = ? AND userID = ?";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, rating);
    preparedStatement.setInt(2, tutorID);
    preparedStatement.setInt(3, userID);
    preparedStatement.executeUpdate();
  }


  public int getTutorsRating(int tutorID, int userID) throws SQLException {
    String state = "SELECT rating FROM " + databaseName + ".tutorrating WHERE"
        + " tutorID = ? AND userID = ?";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, tutorID);
    preparedStatement.setInt(2, userID);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    if (resultSetSubject.next()) {
      return resultSetSubject.getInt("rating");
    } else {
      return -1;
    }
  }

  /**
   * METHOD DESCRIPTION
   */
  public ResultSet getTutorsDescendingByAvgRating() throws SQLException {
    String state = "SELECT tutorID, avg(rating) AS rating FROM " + databaseName
        + ".tutorrating GROUP BY tutorID ORDER BY rating DESC";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  public float getAverageTutorRating(int tutorID) throws SQLException {
    String state = "SELECT avg(rating) FROM " + databaseName + ".tutorrating WHERE"
        + " tutorID = '" + tutorID + "'";
    preparedStatement = connect.prepareStatement(state);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    resultSetSubject.next();
    return resultSetSubject.getFloat("avg(rating)");
  }

  public void removeTutorRating(int tutorID, int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".tutorrating "
          + "WHERE tutorID = ? AND userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, tutorID);
      preparedStatement.setInt(2, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  public void addTutorToSubject(int tutorID, int subjectID) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".tutorstaughtsubjects (tutorID, subjectID) "
        + "VALUES (?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, tutorID);
    preparedStatement.setInt(2, subjectID);
    preparedStatement.executeUpdate();
  }

  /* #####################################################################################
   * ############################# SESSION RELATED METHODS ###############################
   * #####################################################################################*/

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

  public boolean isSessionLive(int sessionID) {
    try {
      String state = "SELECT * FROM " + databaseName + ".livesessions WHERE sessionID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, sessionID);
      ResultSet resultSetSubject = preparedStatement.executeQuery();
      return resultSetSubject.next();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
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
