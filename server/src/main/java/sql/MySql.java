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
 * This Class contains all the methods used to access
 * and manipulate the TutorPoint database. All methods
 * use prepared statements to prevent SQL Injection
 * attacks.
 *
 * <p>In the current state whilst development of the
 * TutorPoint server module is ongoing the database is
 * stored remotely on a Microsoft Azure, Ubuntu Virtual
 * Machine.
 *
 * <p>This means that the Server uses the URL of the
 * Ubuntu machine and that machine has port 3306 open
 * to the public. Once initial development is complete
 * the TutorPoint server module will hosted on the
 * same machine as the MySQL Database and then such
 * a vulnerability can be removed.
 *
 * <p>The database being used it the MariaDB fork of
 * MySQL.
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
  private final String databaseName;
  private Connection connect = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSetUsername = null;
  private ResultSet resultSetEmail = null;
  private static final Logger log = LoggerFactory.getLogger("MySql");

  /**
   * Constructor for the MySql class. Established a conneciton
   * with the remote database and logs in as the user 'java'.
   * This will change to a local database once the server
   * module is being hosted on the Azure servers.
   *
   * @param databaseName
   *        Name of the MySQL Database
   *
   * @throws SQLException
   *         Exception is thrown connection times out
   */
  public MySql(String databaseName) throws SQLException {
    this.databaseName = databaseName;

    try {

      final String Jbc_Driver = "com.mysql.cj.jdbc.Driver";

      /* Database location */
      final String Db_Url = "jdbc:mysql://cubixelservers.uksouth.cloudapp.azure.com:3306/"
          + databaseName;

      /* Database credentials */
      final String User = "java";
      final String Password = "2pWwoP6EBH5U7XpoYuKd";

      /* This will load the MySQL driver, each DB has its own driver */
      Class.forName(Jbc_Driver);

      log.info("Connecting to database " + databaseName);

      /* Setup the connection with the DB */
      connect = DriverManager.getConnection(Db_Url, User, Password);

      log.info("MySql: Successfully connected to database, " + databaseName);
    } catch (ClassNotFoundException cnfe) {
      log.error("MySql: Error while connecting to MySQL Database", cnfe);
    }
  }

  /* #####################################################################################
   * ########################### USER ACCOUNT RELATED METHODS ############################
   * #####################################################################################*/

  /**
   * This inserts a new user into the database and returns
   * {@code true} if successful. This should be called once
   * all checks of user credentials are complete. This
   * includes that the Username doesn't already exist.
   *
   * @param username
   *        The username to identify the user maximum of 20 characters
   *
   * @param email
   *        A standard email, maximum of 100 characters
   *
   * @param hashpw
   *        A sha3_256Hex encrypted password
   *
   * @param tutorStatus
   *        Integer of tutorStatus 1 = true, 0 = false
   *
   * @return {@code true} if user successfully added and {@code false} if not
   *
   */
  public boolean createAccount(String username, String email, String hashpw, int tutorStatus) {
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
   * Performs all necessary clean up to remove an account
   * from the database and then removes the account.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param username
   *        The unique username used to identify the account
   */
  public void removeAccount(int userID, String username) {
    try {
      cleanUpFollowedTutors(userID);
      cleanUpTutorRating(userID);
      cleanUpSubjectRating(userID);

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
   *  Takes a username and sends a query to the DB to check if
   * the user exists.
   *
   * @param username
   *        The unique username used to identify the account
   *
   * @return {@code true} if username matches an existing record
   *         and {@code false} if not
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
   * Takes an email and sends a query to the DB to check if
   * the email exists.
   *
   * @param email
   *        The unique email used to identify the account
   *
   * @return {@code true} if username matches an existing record
   *         and {@code false} if not
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
   * Checks that a provided password is associated with
   * the username provided.
   *
   * @param username
   *        The unique username used to identify the account
   *
   * @param hashedpw
   *        The sha3_256 hashed password to match to the account
   *
   * @return {@code true} if password is correct and {@code false} if not
   */
  public boolean checkUserDetails(String username, String hashedpw) {
    try {
      if (usernameExists(username)) {
        String state = "SELECT * FROM  " + databaseName
            + ".users WHERE BINARY hashedpw = '" + hashedpw + "'";
        preparedStatement = connect.prepareStatement(state);
        resultSetUsername = preparedStatement.executeQuery();
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
   * Returns an email address associated with a provided
   * userID number.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code String} if userID matches an account and
   *         {@code null} if no account found
   */
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

  /**
   * Returns the userID assigned to an account upon account
   * creation.
   *
   * @param username
   *        The unique username used to identify the account
   *
   * @return {@code int} userID > 0 if successful and -1 if not
   */
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

  /**
   * Returns the tutor status of the account based on the
   * provided userID.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code int} TutorStatus 0 or 1 if successful and -1 if not
   */
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

  /**
   * Returns the username of the account associated with
   * the provided userID.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code String} username if successful and {@code null} if not
   */
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

  /**
   * Updates all the information provided that is not null and
   * updates those details based on the userID. The userID never
   * changes and always uniquely identifies users.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param usernameUpdate
   *        A new username, provide the String "null" if no update
   *
   * @param emailAddressUpdate
   *        A new email, provide the String "null" if no update
   *
   * @param hashedpwUpdate
   *        A new password, provide the String "null" if no update
   *
   * @param tutorStatusUpdate
   *        A new tutor status, provide -1 if no update
   */
  public void updateDetails(int userID, String usernameUpdate, String emailAddressUpdate,
      String hashedpwUpdate, int tutorStatusUpdate) {
    // TODO Either throw the exception or return a boolean of success.
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

  /**
   * Add a new Subject to the database and associate the subject
   * with the provided category. If the category for
   * the subject does not already exist then create that
   * category first.
   *
   * @param name
   *        String of the subject name, maximum of 50 characters
   *
   * @param category
   *        String of the subjects category, maximum of 50 characters
   */
  public void addSubject(String name, String category) {
    // TODO Either throw the exception or return a boolean of success.
    try {
      String state = "INSERT INTO " + databaseName + ".subjects (subjectname) "
          + "VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, name);
      preparedStatement.executeUpdate();

      if (!categoryExists(category)) {
        addCategory(category);
      }

      // Could return subjectExists(name);
      linkSubjectAndCategory(getSubjectID(name), getCategoryID(category));
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Performs all necessary clean up to remove a subject from
   * the database and then removes the subject.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   */
  public void removeSubject(int subjectID) {
    // TODO Perform clean up of subject, remove it from the 'subjectcategory' table
    try {
      String state = "DELETE FROM " + databaseName + ".subjects WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.executeUpdate();

      state = "DELETE FROM " + databaseName + ".subjectRating WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.executeUpdate();

      state = "DELETE FROM " + databaseName + ".subjectcategory WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      preparedStatement.executeUpdate();
      log.info("Subject: " + subjectID + "Successfully Removed");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Returns the ResultSet of all subjects within
   * the database.
   *
   * @return {@code ResultSet} of all subjects within the database
   *         in order of subjectID
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public ResultSet getSubjects() throws SQLException {
    String state = "SELECT * FROM " + databaseName + ".subjects";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  /**
   * Overloaded method returns the ResultSet of all subjects within
   * the database that are part of a specific category.
   *
   * @param categoryID
   *        A unique ID that is assigned to a category upon creation
   *
   * @return {@code ResultSet} of all subjects within the database
   *         in order of subjectID
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public ResultSet getSubjects(int categoryID) throws SQLException {
    String state = "SELECT * "
        + "FROM " + databaseName + ".subjects "
        + "INNER JOIN subjectcategory ON subjects.subjectID = subjectcategory.subjectID "
        + "WHERE categoryID = ?";

    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, categoryID);
    return preparedStatement.executeQuery();
  }


  /**
   * Returns the subjectID assigned to an subject upon creation.
   *
   * @param subjectName
   *        The unique String used to identify the subject
   *
   * @return {@code int} subjectID > 0 if successful and -1 if not
   */
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

  /**
   * Returns the subject name of the associated with
   * the provided subjectID.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @return {@code String} subjectName if successful and {@code null} if not
   */
  public String getSubjectName(int subjectID) {
    try {
      String state = "SELECT subjectname FROM " + databaseName + ".subjects WHERE subjectID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      ResultSet resultSetSubjectID = preparedStatement.executeQuery();
      resultSetSubjectID.next();
      return resultSetSubjectID.getString("subjectname");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  /**
   * Creates an subject rating associated between a user and a subject.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param rating
   *        The rating the user has provided
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void addSubjectRating(int subjectID, int userID, int rating) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".subjectrating (subjectID, userID, rating) "
        + "VALUES (?,?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, subjectID);
    preparedStatement.setInt(2, userID);
    preparedStatement.setInt(3, rating);
    preparedStatement.executeUpdate();
  }

  /**
   * Stores the subjects associated with the subjectID as a favourite
   * of the account associated with the userID provided.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void addSubjectToFavourites(int subjectID, int userID) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".favouritesubjects (userID, subjectID) "
        + "VALUES (?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, userID);
    preparedStatement.setInt(2, subjectID);
    preparedStatement.executeUpdate();
  }

  /**
   * Returns the {@code ResultSet} of the subjects an account
   * associated with the provided userID has added to
   * its favourites.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code ResultSet} of favourite subjects
   */
  public ResultSet getFavouriteSubjects(int userID) {
    try {
      String state = "SELECT subjectID FROM "
          + databaseName + ".favouritesubjects WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      return preparedStatement.executeQuery();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  /**
   * Returns the rating that a user has provided of a subject.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return The int rating the user has assigned to a subject
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
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
   * Returns the {@code ResultSet} of all subjects in the database
   * descending by the average rating of all users.
   *
   * @return {@code ResultSet} subjects by rating
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public ResultSet getSubjectsDescendingByRating() throws SQLException {
    String state = "SELECT subjectID, avg(rating) AS rating FROM " + databaseName
        + ".subjectrating GROUP BY subjectID ORDER BY rating DESC";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  /**
   * Returns a specific subjects average rating based on all
   * users ratings of that subject.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @return The average rating of the subject
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public float getAverageSubjectRating(int subjectID) throws SQLException {
    String state = "SELECT avg(rating) FROM " + databaseName + ".subjectrating WHERE"
        + " subjectID = '" + subjectID + "'";
    preparedStatement = connect.prepareStatement(state);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    resultSetSubject.next();
    return resultSetSubject.getFloat("avg(rating)");
  }

  /**
   * Removes a rating a user has provided of a subject.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public void removeSubjectRating(int subjectID, int userID) {
    // TODO Either throw the exception or return a boolean of success.
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

  /**
   * Removed all ratings provided by a user.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public void cleanUpSubjectRating(int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".subjectrating "
          + "WHERE userID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Takes String subjectName and sends a query to the DB to check if
   * the subject exists.
   *
   * @param subjectName
   *        The unique name used to identify the subject
   *
   * @return {@code true} if subjectName matches an existing record
   *         and {@code false} if not
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

  /**
   * Associates a subject with a category.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param categoryID
   *        A unique ID that is assigned to a category upon creation
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void linkSubjectAndCategory(int subjectID, int categoryID) throws SQLException {
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

  /**
   * Adds a new category to the database. This does not perform
   * any checks and the categoryExists method should be called
   * first to prevent copies.
   *
   * @param categoryName
   *        String of the category name, maximum of 50 characters
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void addCategory(String categoryName) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".category (categoryname) "
        + "VALUES (?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setString(1, categoryName);
    preparedStatement.executeUpdate();
  }

  /**
   * Returns the unique ID assigned to a category upon creation.
   *
   * @param categoryName
   *        String of the category name, maximum of 50 characters
   *
   * @return {@code int} categoryID > 0 if successful and -1 if not
   */
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

  /**
   * Takes an categoryName and sends a query to the DB to check if
   * the category exists.
   *
   * @param categoryName
   *        The unique email used to identify the account
   *
   * @return {@code true} if category matches an existing record
   *         and {@code false} if not
   */
  public boolean categoryExists(String categoryName) {
    try {
      String state = "SELECT * FROM " + databaseName + ".category WHERE BINARY categoryname = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setString(1, categoryName);
      ResultSet resultSetSubject = preparedStatement.executeQuery();
      return resultSetSubject.next();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return false;
    }
  }

  /**
   * Returns the category that a subject is associated with.
   * Currently only returns the top record. If a subject is
   * associated with more than one category this will need
   * to return the result set.
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @return {@code String} The category of the subject or {@code null} if error
   */
  public String getSubjectCategory(int subjectID) {
    try {
      String state = "SELECT categoryname "
          + "FROM " + databaseName + ".category "
          + "INNER JOIN subjectcategory ON category.categoryID = subjectcategory.categoryID "
          + "WHERE subjectID = ?";

      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, subjectID);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return resultSet.getString("categoryname");
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  /* #####################################################################################
   * ############################### TUTOR RELATED METHODS ###############################
   * #####################################################################################*/

  /**
   * Sets an account on the database as being currently live.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  private void setTutorIsLive(int userID) {
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

  /**
   * Sets an account on the database as being currently not live.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  private void setTutorNotLive(int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".livetutors WHERE (userID) VALUES (?)";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Returns the list of tutors that a user has added to their
   * followed tutors that are currently live.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code ResultSet} live tutors that the user follows
   */
  public ResultSet getLiveTutors(int userID) {
    try {
      String state = "SELECT tutorID "
          + "FROM " + databaseName + ".livetutors "
          + "INNER JOIN followedtutors ON livetutors.userID = followedtutors.tutorID "
          + "WHERE userID = ?";

      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      return preparedStatement.executeQuery();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
      return null;
    }
  }

  /**
   * Adds the account matching the tutorID to the followed tutors of the
   * account matching the userID.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   */
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

  /**
   * Returns the list of userID's of the accounts followed
   * by the userID provided.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return {@code ResultSet} userID of the tutors that the user follows
   */
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

  /**
   * Removes a tutor account from the followed accounts
   * of the provided userID.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   */
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

  /**
   * Removes all followed accounts of the account
   * associated with the provided userID.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public void cleanUpFollowedTutors(int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".followedtutors "
          + "WHERE userID = ? OR tutorID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.setInt(2, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Creates an tutor rating associated between a user and a tutor.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param rating
   *        The rating the user has provided
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void addTutorRating(int tutorID, int userID, int rating) throws SQLException {
    String state = "INSERT INTO " + databaseName + ".tutorrating (tutorID, userID, rating) "
        + "VALUES (?,?,?)";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, tutorID);
    preparedStatement.setInt(2, userID);
    preparedStatement.setInt(3, rating);
    preparedStatement.executeUpdate();
  }

  /**
   * Updates the rating a user has provided of a tutor account.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param rating
   *        The rating the user has provided
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public void updateTutorRating(int tutorID, int userID, int rating) throws SQLException {
    String state = "UPDATE " + databaseName
        + ".tutorrating SET rating = ? WHERE tutorID = ? AND userID = ?";
    preparedStatement = connect.prepareStatement(state);
    preparedStatement.setInt(1, rating);
    preparedStatement.setInt(2, tutorID);
    preparedStatement.setInt(3, userID);
    preparedStatement.executeUpdate();
  }

  /**
   * Gets the rating of a specific user of a specific tutor.
   *
   /**
   * Updates the rating a user has provided of a tutor account.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @return The rating the user has provided of the tutor
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
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
   * Returns the {@code ResultSet} of all tutors in the database
   * descending by the average rating of all users.
   *
   * @return {@code ResultSet} tutors by rating
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public ResultSet getTutorsDescendingByAvgRating() throws SQLException {
    String state = "SELECT tutorID, avg(rating) AS rating FROM " + databaseName
        + ".tutorrating GROUP BY tutorID ORDER BY rating DESC";
    preparedStatement = connect.prepareStatement(state);
    return preparedStatement.executeQuery();
  }

  /**
   * Returns a specific tutors average rating based on all
   * users ratings of that tutor.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @return The average rating of the tutor
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
  public float getAverageTutorRating(int tutorID) throws SQLException {
    String state = "SELECT avg(rating) FROM " + databaseName + ".tutorrating WHERE"
        + " tutorID = '" + tutorID + "'";
    preparedStatement = connect.prepareStatement(state);
    ResultSet resultSetSubject = preparedStatement.executeQuery();
    resultSetSubject.next();
    return resultSetSubject.getFloat("avg(rating)");
  }

  /**
   * Removed a users rating of a tutor account.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
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

  /**
   * Removes all ratings that a user has provided of
   * tutors in the database.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public void cleanUpTutorRating(int userID) {
    try {
      String state = "DELETE FROM " + databaseName + ".tutorrating "
          + "WHERE userID = ? OR tutorID = ?";
      preparedStatement = connect.prepareStatement(state);
      preparedStatement.setInt(1, userID);
      preparedStatement.setInt(2, userID);
      preparedStatement.executeUpdate();
    } catch (SQLException sqle) {
      log.warn("Error accessing MySQL Database", sqle);
    }
  }

  /**
   * Links a tutor account with a subject that they teach.
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param subjectID
   *        A unique ID that is assigned to a subject upon creation
   *
   * @throws SQLException
   *         Thrown if connection times out or database error
   */
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

  /**
   * Creats a record of a session to be used whilst the session is live.
   * It contains the sessions unique ID number. The tutor teaching
   * that session and the thumbnail path being used for that
   * sessions image. It also sets the tutors live status.
   *
   * @param sessionID
   *        A unique ID so that others can join the session
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param sessionName
   *        The name the tutor has provided for the session
   *
   * @param thumbnailPath
   *        A path to a thumbnail the tutor has provided for that session
   */
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

  /**
   * Check if a session is live based on the session ID number.
   *
   * @param sessionID
   *        A unique ID so that others can join the session
   *
   * @return {@code true} if the session is live and {@code false}
   *         if not
   */
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

  /**
   * Removes the session from the live sessions table. And
   * updates the tutors live status.
   *
   * @param sessionID
   *        A unique ID so that others can join the session
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   */
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