package sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the test Class for the MySql class.
 * It generates a test database on the Azure
 * Virtual Machine, runs tests on all methods
 * of the MySql Class and then removes the test
 * database at the end.
 *
 * @author James Gardner
 * @see MySql
 * @see MySqlQuickBuild
 */
public class MySqlTest {

  private static MySql db = null;

  private final String username = "Test";
  private final String email = "test@cubixel.com";
  private final String password = "testpAssw_ord";
  private final int tutorStatus = 1;

  private Account account;

  private static final Logger log = LoggerFactory.getLogger("MySqlTest");


  /**
   * Builds a test database on the Azure Virtual Machine.
   */
  @BeforeAll
  public static void createDatabaseTestServer() {
    /*
     * Creating a server object on which to test, this
     * is running on the Azure servers on a virtual machine
     * by currently. This will change to the local machine
     * the server is running on once the server is finished
     * in development.
     *  */
    final String Jbc_Driver = "com.mysql.cj.jdbc.Driver";
    final String Db_Url = "jdbc:mysql://cubixelservers.uksouth.cloudapp.azure.com:3306/";

    //  Database credentials
    final String User = "java";
    final String Password = "2pWwoP6EBH5U7XpoYuKd";

    Connection conn;
    Statement stmt;

    log.info("Creating test database");

    try {
      Class.forName(Jbc_Driver);

      conn = DriverManager.getConnection(Db_Url, User, Password);

      stmt = conn.createStatement();
      String sql = "CREATE DATABASE tutorpointtest";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.users ("
          + "userID INT unsigned NOT NULL AUTO_INCREMENT, "
          + "username VARCHAR(20), "
          + "email VARCHAR(100), "
          + "hashedpw VARCHAR(64), "
          + "istutor INT, "
          + "PRIMARY KEY (userID)); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.subjects ("
          + "subjectID INT unsigned NOT NULL AUTO_INCREMENT, "
          + "subjectname VARCHAR(50), "
          + "PRIMARY KEY (subjectID)); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.livetutors ("
          + "userID INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.favouritesubjects ("
          + "userID INT, "
          + "subjectID INT);";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.livesessions ("
          + "sessionID INT, "
          + "tutorID INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.followedtutors ("
          + "userID INT, "
          + "tutorID INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.subjectrating ("
          + "subjectID INT, "
          + "userID INT, "
          + "rating INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.tutorrating ("
          + "tutorID INT, "
          + "userID INT, "
          + "rating INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.tutorstaughtsubjects ("
          + "tutorID INT, "
          + "subjectID INT);";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.category ("
          + "categoryID INT unsigned NOT NULL AUTO_INCREMENT, "
          + "categoryname VARCHAR(50), "
          + "PRIMARY KEY (categoryID)); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.subjectcategory ("
          + "subjectID INT, "
          + "categoryID INT);";

      stmt.executeUpdate(sql);
      conn.close();
      db = new MySql("tutorpointtest");

      log.info("Successfully created test database");
    } catch (SQLException | ClassNotFoundException e) {
      log.error("Failed to create test database", e);
      fail();
    }
  }

  /**
   * Removes the test database from the Azure servers
   * after all tests are completed.
   */
  @AfterAll
  public static void removeDatabaseTestServer() {
    final String Jbc_Driver = "com.mysql.cj.jdbc.Driver";
    final String Db_Url = "jdbc:mysql://cubixelservers.uksouth.cloudapp.azure.com:3306/";

    //  Database credentials
    final String User = "java";
    final String Password = "2pWwoP6EBH5U7XpoYuKd";

    Connection conn;
    Statement stmt;

    log.info("Removing test database");

    try {
      Class.forName(Jbc_Driver);

      conn = DriverManager.getConnection(Db_Url, User, Password);

      stmt = conn.createStatement();
      String sql = "DROP DATABASE tutorpointtest";
      stmt.executeUpdate(sql);

      conn.close();

      log.info("Successfully removed test database");

    } catch (SQLException | ClassNotFoundException e) {
      log.error("Failed to remove the test database", e);
      fail();
    }
  }

  /**
   * Creates an account to test on before each test.
   */
  @BeforeEach
  public void setUp() {
    db.createAccount(username, email, password, tutorStatus);
    account = new Account(db.getUserID(username), username, email,
        password, tutorStatus, 0);
  }

  @AfterEach
  public void cleanUp() {
    db.removeAccount(account.getUserID(), username);
  }

  @Test
  public void createAccountTest() {
    String username = "usernametest";
    String email = "someemailtest@test.com";
    String hashpw = "passwordtest";
    int tutorStatus = 1;

    assertTrue(db.createAccount(username, email, hashpw, tutorStatus));

    String usernameNew = "usernameUnique";
    String emailNew = "someotheremail@test.com";

    assertTrue(db.createAccount(usernameNew, emailNew, hashpw, tutorStatus));
  }

  @Test
  public void removeAccount() {
    String username = "usernametest";
    String email = "someemailtest@test.com";
    String hashpw = "passwordtest";
    int tutorStatus = 1;

    assertTrue(db.createAccount(username, email, hashpw, tutorStatus));

    int userID = db.getUserID(username);

    db.removeAccount(userID, username);

    assertFalse(db.usernameExists(username));
  }

  @Test
  public void usernameExistsTest() {
    assertTrue(db.usernameExists(username));
    assertFalse(db.usernameExists("notindatabase"));
  }

  @Test
  public void emailExistsTest() {
    assertTrue(db.emailExists(email));
    assertFalse(db.emailExists("none@test.com"));
  }

  @Test
  public void checkUserDetailsTest() {
    assertTrue(db.checkUserDetails(username, password));
    assertFalse(db.checkUserDetails(username, "incorrectPassword"));
  }

  @Test
  public void getEmailAddressTest() {
    assertEquals(email, db.getEmailAddress(account.getUserID()));
    assertNotEquals(email, db.getEmailAddress(20));
  }

  @Test
  public void getUserIdTest() {
    assertEquals(account.getUserID(), db.getUserID(username));
    db.createAccount("newUser", "email", "password", 0);
    assertNotEquals(1, db.getUserID("newUser"));
    db.removeAccount(db.getUserID("newUser"),"newUser");
  }

  @Test
  public void getTutorStatusTest() {
    assertEquals(tutorStatus, db.getTutorStatus(account.getUserID()));
  }

  @Test
  public void getUsernameTest() {
    int invalidID = -1;
    assertEquals(username, db.getUsername(account.getUserID()));
    assertNull(db.getUsername(invalidID));
  }

  @Test
  public void updateDetailsTest() {
    assertEquals(username, db.getUsername(account.getUserID()));
    assertEquals(email, db.getEmailAddress(account.getUserID()));
    assertEquals(tutorStatus, db.getTutorStatus(account.getUserID()));

    db.updateDetails(account.getUserID(), "NewName",
        "NewEmail@test.com","NewPassword", 0);

    account.setUsername(db.getUsername(account.getUserID()));

    assertEquals("NewName", account.getUsername());
    assertEquals("NewEmail@test.com", db.getEmailAddress(account.getUserID()));
    assertEquals(0, db.getTutorStatus(account.getUserID()));
  }

  @Test
  public void addSubjectTest() {
    String newSubjectName = "newTestSubject";
    String newCategory = "newCategory";
    String secondNewSubject = "secondTestSubject";
    db.addSubject(newSubjectName, newCategory);
    db.addSubject(secondNewSubject, newCategory);
    int subjectID = db.getSubjectID(newSubjectName);
    assertEquals("newCategory", db.getSubjectCategory(subjectID));
    assertNotEquals(subjectID, db.getSubjectID("secondTestSubject"));
    db.removeSubject(db.getSubjectID(newSubjectName));
    db.removeSubject(db.getSubjectID(secondNewSubject));
    // TODO Test adding a subject and category name that are too long.
  }

  @Test
  public void removeSubjectTest() {
    String subjectName = "TestSubject";
    String category = "Category";
    assertEquals(-1, db.getSubjectID(subjectName));
    db.addSubject(subjectName, category);
    int subjectID = db.getSubjectID(subjectName);
    assertNotEquals(-1, subjectID);
    db.removeSubject(subjectID);
    assertEquals(-1, db.getSubjectID(subjectName));
  }

  @Test
  public void getSubjectsTest() {
    String[] subjects = {"TestOne", "TestTwo", "TestThree"};
    String category = "Category";
    for (String subject : subjects) {
      db.addSubject(subject, category);
    }
    try {
      ResultSet databaseSubjects = db.getSubjects();
      int i = 0;
      while (databaseSubjects.next()) {
        assertEquals(subjects[i], databaseSubjects.getString("subjectName"));
        i++;
        db.removeSubject(databaseSubjects.getInt("subjectID"));
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getSubjectsBasedOnCategoryTest() {
    String[] subjects = {"TestOne", "TestTwo", "TestThree"};
    String category = "Category";
    String[] subjectsTwo = {"TestFour", "TestFive", "TestSix"};
    String categoryTwo = "CategoryTwo";
    for (String subject : subjects) {
      db.addSubject(subject, category);
    }
    for (String subject : subjectsTwo) {
      db.addSubject(subject, categoryTwo);
    }
    try {
      ResultSet databaseSubjects = db.getSubjects(db.getCategoryID(category));
      int i = 0;
      while (databaseSubjects.next()) {
        assertEquals(subjects[i], databaseSubjects.getString("subjectName"));
        assertNotEquals(subjectsTwo[i], databaseSubjects.getString("subjectName"));
        i++;
      }
      databaseSubjects = db.getSubjects(db.getCategoryID(categoryTwo));
      i = 0;
      while (databaseSubjects.next()) {
        assertEquals(subjectsTwo[i], databaseSubjects.getString("subjectName"));
        assertNotEquals(subjects[i], databaseSubjects.getString("subjectName"));
        i++;
      }
      databaseSubjects = db.getSubjects();
      while (databaseSubjects.next()) {
        db.removeSubject(databaseSubjects.getInt("subjectID"));
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getSubjectIdAndNameTest() {
    db.addSubject("Test", "Test");
    assertEquals(db.getSubjectName(db.getSubjectID("Test")), "Test");
    db.removeSubject(db.getSubjectID("Test"));
  }

  @Test
  public void addSubjectRatingTest() {
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    try {
      db.addSubjectRating(subjectID, account.getUserID(), 5);
      assertEquals(5, db.getUsersSubjectRating(subjectID, account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    db.removeSubject(db.getSubjectID("Test"));
  }

  @Test
  public void addSubjectToFavouritesTest() {
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    try {
      db.addSubjectToFavourites(subjectID, account.getUserID());
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    ResultSet resultSet = db.getFavouriteSubjects(account.getUserID());
    try {
      if (resultSet.next()) {
        int result = resultSet.getInt("subjectID");
        assertEquals(subjectID, result);
      } else {
        log.error("No favourite subject");
        fail();
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    db.removeSubject(db.getSubjectID("Test"));
  }

  @Test
  public void removeFromFavouriteSubjectsTest() {
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    try {
      db.addSubjectToFavourites(subjectID, account.getUserID());
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    ResultSet resultSet = db.getFavouriteSubjects(account.getUserID());
    try {
      if (resultSet.next()) {
        db.removeFromFavouriteSubjects(account.getUserID(), subjectID);
        resultSet = db.getFavouriteSubjects(account.getUserID());
        assertFalse(resultSet.next());
      } else {
        log.error("No favourite subject");
        fail();
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getSubjectsDescendingByRatingTest() {
    db.addSubject("TestRatingFive", "Test");
    db.addSubject("TestRatingOne", "Test");
    db.addSubject("TestRatingThree", "Test");
    db.addSubject("TestRatingTwo", "Test");
    db.addSubject("TestRatingFour", "Test");
    int[] subjectIds = new int[5];
    subjectIds[0] = db.getSubjectID("TestRatingFive");
    subjectIds[1] = db.getSubjectID("TestRatingFour");
    subjectIds[2] = db.getSubjectID("TestRatingThree");
    subjectIds[3] = db.getSubjectID("TestRatingTwo");
    subjectIds[4] = db.getSubjectID("TestRatingOne");
    try {
      db.addSubjectToFavourites(subjectIds[3], account.getUserID());
      db.addSubjectRating(subjectIds[3], account.getUserID(), 2);
      db.addSubjectToFavourites(subjectIds[1], account.getUserID());
      db.addSubjectRating(subjectIds[1], account.getUserID(), 4);
      db.addSubjectToFavourites(subjectIds[2], account.getUserID());
      db.addSubjectRating(subjectIds[2], account.getUserID(), 3);
      db.addSubjectToFavourites(subjectIds[4], account.getUserID());
      db.addSubjectRating(subjectIds[4], account.getUserID(), 1);
      db.addSubjectToFavourites(subjectIds[0], account.getUserID());
      db.addSubjectRating(subjectIds[0], account.getUserID(), 5);
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }

    try {
      ResultSet resultSet = db.getSubjectsDescendingByRating();
      for (int subjectID: subjectIds) {
        resultSet.next();
        assertEquals(subjectID, resultSet.getInt("subjectID"));
        db.removeSubject(subjectID);
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getAverageSubjectRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void removeSubjectRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void cleanUpSubjectRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void subjectExistsTest() {
    //TODO Complete Test
  }

  @Test
  public void linkSubjectAndCategoryTest() {
    //TODO Complete Test
  }

  @Test
  public void addCategoryTest() {
    //TODO Complete Test
  }

  @Test
  public void getCategoryIdTest() {
    //TODO Complete Test
  }

  @Test
  public void categoryExistsTest() {
    //TODO Complete Test
  }

  @Test
  public void getSubjectCategoryTest() {
    //TODO Complete Test
  }

  @Test
  public void setTutorIsLiveTest() {
    //TODO Complete Test
  }

  @Test
  public void setTutorNotLiveTest() {
    //TODO Complete Test
  }

  @Test
  public void getLiveTutorsTest() {
    //TODO Complete Test
  }

  @Test
  public void addToFollowedTutorsTest() {
    //TODO Complete Test
  }

  @Test
  public void getFollowedTutorsTest() {
    //TODO Complete Test
  }

  @Test
  public void removeFromFollowedTutorsTest() {
    //TODO Complete Test
  }

  @Test
  public void cleanUpFollowedTutorsTest() {
    //TODO Complete Test
  }

  @Test
  public void addTutorRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void updateTutorRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void getTutorsRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void getTutorsDescendingByAvgRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void getAverageTutorRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void removeTutorRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void cleanUpTutorRatingTest() {
    //TODO Complete Test
  }

  @Test
  public void addTutorToSubjectTest() {
    //TODO Complete Test
  }

  @Test
  public void setLiveSessionTest() {
    //TODO Complete Test
  }

  @Test
  public void isSessionLiveTest() {
    //TODO Complete Test
  }

  @Test
  public void endLiveSessionTest() {
    //TODO Complete Test
  }
}