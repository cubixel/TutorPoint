package sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    try {
      account = new Account(db.getUserID(username), username, email,
          password, tutorStatus, 0);
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
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

    int userID = 0;
    try {
      userID = db.getUserID(username);
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }

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
    try {
      assertEquals(email, db.getEmailAddress(account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getEmailAddress(20)
    );
  }

  @Test
  public void getUserIdTest() {
    try {
      assertEquals(account.getUserID(), db.getUserID(username));
      db.createAccount("newUser", "email", "password", 0);
      assertNotEquals(1, db.getUserID("newUser"));
      db.removeAccount(db.getUserID("newUser"),"newUser");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getUserID("NotInServer")
    );
  }

  @Test
  public void getTutorStatusTest() {
    int invalidID = -1;
    try {
      assertEquals(tutorStatus, db.getTutorStatus(account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getTutorStatus(invalidID)
    );
  }

  @Test
  public void getUsernameTest() {
    int invalidID = -1;
    try {
      assertEquals(username, db.getUsername(account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getUsername(invalidID)
    );
  }

  @Test
  public void updateDetailsTest() {
    try {
      assertEquals(username, db.getUsername(account.getUserID()));
      assertEquals(email, db.getEmailAddress(account.getUserID()));
      assertEquals(tutorStatus, db.getTutorStatus(account.getUserID()));

      db.updateDetails(account.getUserID(), "NewName",
          "NewEmail@test.com","NewPassword", 0);

      account.setUsername(db.getUsername(account.getUserID()));

      assertEquals("NewName", account.getUsername());
      assertEquals("NewEmail@test.com", db.getEmailAddress(account.getUserID()));
      assertEquals(0, db.getTutorStatus(account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }

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
    db.createAccount("UserForTest", "Email", "password", 1);
    int userID = 0;
    try {
      userID = db.getUserID("UserForTest");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    try {
      db.addSubjectRating(subjectID, account.getUserID(), 5);
      db.addSubjectRating(subjectID, userID, 3);
      float result = db.getAverageSubjectRating(subjectID);
      float expected = (float) (5 + 3) / 2;
      assertEquals(expected, result);
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    db.removeSubject(subjectID);
    db.removeAccount(userID, "UserForTest");
  }

  @Test
  public void removeSubjectRatingTest() {
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    int rating = 5;
    try {
      db.addSubjectRating(subjectID, account.getUserID(), rating);
      int result = db.getUsersSubjectRating(subjectID, account.getUserID());
      assertEquals(rating, result);
      db.removeSubjectRating(subjectID, account.getUserID());
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getUsersSubjectRating(subjectID, account.getUserID())
    );
    db.removeSubject(subjectID);
  }

  @Test
  public void cleanUpSubjectRatingTest() {
    db.addSubject("Test", "Test");
    db.addSubject("TestTwo", "Test");
    db.addSubject("TestThree", "Test");
    int subjectIdOne = db.getSubjectID("Test");
    int subjectIdTwo = db.getSubjectID("TestTwo");
    int subjectIdThree = db.getSubjectID("TestThree");
    int rating = 5;
    try {
      db.addSubjectRating(subjectIdOne, account.getUserID(), rating);
      db.addSubjectRating(subjectIdTwo, account.getUserID(), rating);
      db.addSubjectRating(subjectIdThree, account.getUserID(), rating);
      assertEquals(rating, db.getUsersSubjectRating(subjectIdOne, account.getUserID()));
      assertEquals(rating, db.getUsersSubjectRating(subjectIdTwo, account.getUserID()));
      assertEquals(rating, db.getUsersSubjectRating(subjectIdThree, account.getUserID()));
      db.cleanUpSubjectRating(account.getUserID());
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertThrows(SQLException.class, () ->
        db.getUsersSubjectRating(subjectIdOne, account.getUserID())
    );
    assertThrows(SQLException.class, () ->
        db.getUsersSubjectRating(subjectIdTwo, account.getUserID())
    );
    assertThrows(SQLException.class, () ->
        db.getUsersSubjectRating(subjectIdThree, account.getUserID())
    );
    db.removeSubject(subjectIdOne);
    db.removeSubject(subjectIdTwo);
    db.removeSubject(subjectIdThree);
  }

  @Test
  public void subjectExistsTest() {
    assertFalse(db.subjectExists("NotRealSubject"));
    db.addSubject("Test", "Test");
    assertTrue(db.subjectExists("Test"));
    int subjectID = db.getSubjectID("Test");
    db.removeSubject(subjectID);
  }

  @Test
  public void isSubjectFollowedTest() {
    db.addSubject("Test", "Test");
    int subjectID = db.getSubjectID("Test");
    assertFalse(db.isSubjectFollowed(subjectID, account.getUserID()));
    try {
      db.addSubjectToFavourites(subjectID, account.getUserID());
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertTrue(db.isSubjectFollowed(subjectID, account.getUserID()));
    db.removeSubject(subjectID);
  }

  @Test
  public void addCategoryTest() {
    assertFalse(db.categoryExists("TestCategory"));
    try {
      db.addCategory("TestCategory");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
    assertTrue(db.categoryExists("TestCategory"));
  }

  @Test
  public void getCategoryIdTest() {
    String categoryName = "TestIDCategory";
    try {
      db.addCategory(categoryName);
      int categoryID = db.getCategoryID(categoryName);
      assertTrue(categoryID > 0);
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getSubjectCategoryTest() {
    String categoryName = "TestSubjectCategory";
    db.addSubject("Test", categoryName);
    int subjectID = db.getSubjectID("Test");
    String result = db.getSubjectCategory(subjectID);
    assertEquals(categoryName, result);
    db.removeSubject(subjectID);
  }

  @Test
  public void addFollowedAndGetLiveTutorsTest() {
    db.createAccount("TestAccount", "TestEmail", "Password", 1);
    try {
      int userID = db.getUserID("TestAccount");
      ResultSet resultSet = db.getLiveTutors(userID);
      assertFalse(resultSet.next());
      db.addToFollowedTutors(userID, account.getUserID());
      db.startLiveSession(account.getUserID(), account.getUserID());
      resultSet = db.getLiveTutors(userID);
      assertTrue(resultSet.next());
      assertEquals(account.getUserID(), resultSet.getInt("tutorID"));
      db.removeAccount(userID, "TestAccount");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getFollowedTutorsTest() {
    try {
      String[] testAccountNames = {"TestAccountOne", "TestAccountTwo",
          "TestAccountThree", "TestAccountFour"};
      int[] userIds = new int[4];
      int i = 0;
      for (String name: testAccountNames) {
        db.createAccount(name, "TestEmail", "Password", 1);
        userIds[i] = db.getUserID(name);
        i++;
      }
      db.addToFollowedTutors(account.getUserID(), userIds[2]);
      db.addToFollowedTutors(account.getUserID(), userIds[3]);
      ResultSet resultSet = db.getFollowedTutors(account.getUserID());
      resultSet.next();
      assertEquals(userIds[2], resultSet.getInt("tutorID"));
      resultSet.next();
      assertEquals(userIds[3], resultSet.getInt("tutorID"));
      i = 0;
      for (String name: testAccountNames) {
        db.removeAccount(userIds[i], name);
        i++;
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void removeFromFollowedTutorsTest() {
    try {
      db.createAccount("TestAccount", "TestEmail", "Password", 1);
      int userID = db.getUserID("TestAccount");
      ResultSet resultSet = db.getFollowedTutors(account.getUserID());
      assertFalse(resultSet.next());
      db.addToFollowedTutors(account.getUserID(), userID);
      resultSet = db.getFollowedTutors(account.getUserID());
      assertTrue(resultSet.next());
      db.removeFromFollowedTutors(account.getUserID(), userID);
      resultSet = db.getFollowedTutors(account.getUserID());
      assertFalse(resultSet.next());
      db.removeAccount(userID, "TestAccount");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void addTutorRatingTest() {
    try {
      db.createAccount("TestAccount", "TestEmail", "Password", 1);
      int userID = db.getUserID("TestAccount");
      int rating = 5;
      int newRating = 3;
      db.addTutorRating(userID, account.getUserID(), rating);
      assertEquals(rating, db.getTutorsRating(userID, account.getUserID()));
      db.addTutorRating(userID, account.getUserID(), newRating);
      assertEquals(newRating, db.getTutorsRating(userID, account.getUserID()));
      db.removeAccount(userID, "TestAccount");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getTutorsDescendingByAvgRatingTest() {
    try {
      String[] testAccountNames = {"TestAccountOne", "TestAccountTwo",
          "TestAccountThree", "TestAccountFour", "TestAccountFive"};
      int[] userIds = new int[5];
      int i = 0;
      for (String name: testAccountNames) {
        db.createAccount(name, "TestEmail", "Password", 1);
        userIds[i] = db.getUserID(name);
        i++;
      }
      db.addTutorRating(userIds[0], account.getUserID(), 5);
      db.addTutorRating(userIds[1], account.getUserID(), 4);
      db.addTutorRating(userIds[2], account.getUserID(), 1);
      db.addTutorRating(userIds[3], account.getUserID(), 3);
      db.addTutorRating(userIds[4], account.getUserID(), 2);

      ResultSet resultSet = db.getTutorsDescendingByAvgRating();
      resultSet.next();
      assertEquals(userIds[0], resultSet.getInt("tutorID"));
      resultSet.next();
      assertEquals(userIds[1], resultSet.getInt("tutorID"));
      resultSet.next();
      assertEquals(userIds[3], resultSet.getInt("tutorID"));
      resultSet.next();
      assertEquals(userIds[4], resultSet.getInt("tutorID"));
      resultSet.next();
      assertEquals(userIds[2], resultSet.getInt("tutorID"));

      i = 0;
      for (String name: testAccountNames) {
        db.removeAccount(userIds[i], name);
        i++;
      }
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void getAverageTutorRatingTest() {
    try {
      db.createAccount("TestAccountOne", "TestEmail", "Password", 0);
      int userIdOne = db.getUserID("TestAccountOne");
      db.createAccount("TestAccountTwo", "TestEmail", "Password", 0);
      int userIdTwo = db.getUserID("TestAccountTwo");
      db.addTutorRating(account.getUserID(), userIdOne, 5);
      db.addTutorRating(account.getUserID(), userIdTwo, 3);
      float result = db.getAverageTutorRating(account.getUserID());
      float expected = (float) (5 + 3) / 2;
      assertEquals(expected, result);
      db.removeAccount(userIdOne, "TestAccountOne");
      db.removeAccount(userIdTwo, "TestAccountTwo");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void removeTutorRatingTest() {
    try {
      db.createAccount("TestAccountOne", "TestEmail", "Password", 0);
      int userIdOne = db.getUserID("TestAccountOne");
      assertEquals(-1, db.getTutorsRating(account.getUserID(), userIdOne));
      db.addTutorRating(account.getUserID(), userIdOne, 5);
      assertEquals(5, db.getTutorsRating(account.getUserID(), userIdOne));
      db.removeTutorRating(account.getUserID(), userIdOne);
      assertEquals(-1, db.getTutorsRating(account.getUserID(), userIdOne));
      db.removeAccount(userIdOne, "TestAccountOne");
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }

  @Test
  public void startLiveSessionTest() {
    try {
      assertFalse(db.isSessionLive(account.getUserID()));
      assertFalse(db.isTutorLive(account.getUserID()));
      db.startLiveSession(account.getUserID(), account.getUserID());
      assertTrue(db.isSessionLive(account.getUserID()));
      assertTrue(db.isTutorLive(account.getUserID()));
      db.endLiveSession(account.getUserID(), account.getUserID());
      assertFalse(db.isSessionLive(account.getUserID()));
      assertFalse(db.isTutorLive(account.getUserID()));
    } catch (SQLException sqlException) {
      log.error("Failed to access Database", sqlException);
      fail();
    }
  }
}