package sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import model.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CLASS DESCRIPTION.
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySqlTest {

  private static MySql db = null;

  private String username = "Test";
  private String email = "test@cubixel.com";
  private String password = "testpAssw_ord";
  private int tutorStatus = 1;

  private Account account;

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  @BeforeAll
  public static void createDatabaseTestServer() throws Exception {
    /*
     * Creating a server object on which to test, this
     * is running on localhost by default an arbitrarily
     * chosen port 5000.
     *  */
    final String Jbc_Driver = "com.mysql.cj.jdbc.Driver";
    final String Db_Url = "jdbc:mysql://cubixel.ddns.net:52673/";

    //  Database credentials
    final String User = "java";
    final String Password = "2pWwoP6EBH5U7XpoYuKd";

    Connection conn;
    Statement stmt;

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
          + "thumbnailpath VARCHAR(300), "
          + "thumbnailfilename VARCHAR(30), "
          + "coverphotopath VARCHAR(300), "
          + "coverphotofilename VARCHAR(30), "
          + "PRIMARY KEY (subjectID)); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.livetutors ("
          + "userID INT); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.favouritesubjects ("
          + "favouriteID INT unsigned NOT NULL AUTO_INCREMENT, "
          + "userID INT, "
          + "subjectID INT, "
          + "PRIMARY KEY (favouriteID)); ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.livesessions ("
          + "sessionID INT, "
          + "tutorID INT,"
          + "sessionname VARCHAR(50),"
          + "thumbnailpath VARCHAR(300)); ";

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
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    db = new MySql("tutorpointtest");
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  @AfterAll
  public static void destroyDatabaseTestServer() {
    final String Jbc_Driver = "com.mysql.cj.jdbc.Driver";
    final String Db_Url = "jdbc:mysql://cubixel.ddns.net:52673/";

    //  Database credentials
    final String User = "java";
    final String Password = "2pWwoP6EBH5U7XpoYuKd";


    Connection conn;
    Statement stmt;

    try {
      Class.forName(Jbc_Driver);

      conn = DriverManager.getConnection(Db_Url, User, Password);

      stmt = conn.createStatement();
      String sql = "DROP DATABASE tutorpointtest";
      stmt.executeUpdate(sql);

      conn.close();

    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

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

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
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

    int userID = db.getUserID(username);
    db.removeAccount(userID, username);

    userID = db.getUserID(usernameNew);
    db.removeAccount(userID, usernameNew);
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

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
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
}
