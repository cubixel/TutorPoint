/*
 * MySQLTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */

package sql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
import services.enums.AccountDetailsUpdate;

/**
 * CLASS DESCRIPTION.
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySqlTest {

  private static MySql db = null;

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  //@BeforeAll
  public static void createServer() throws Exception {
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
          + "username VARCHAR(20), "
          + "email VARCHAR(100), "
          + "hashedpw VARCHAR(64), "
          + "istutor CHAR(1)) ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.subjects ("
          + "id INT(5) unsigned NOT NULL AUTO_INCREMENT, "
          + "subjectname VARCHAR(50),"
          + "thumbnailpath VARCHAR(300), "
          + "filename VARCHAR(30),"
          + "primary key (id)) ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.livetutors ("
          + "username VARCHAR(20)) ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.favouritesubjects ("
          + "username VARCHAR(20), "
          + "subjectname VARCHAR(50)) ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.courses ("
          + "id INT(5) unsigned NOT NULL AUTO_INCREMENT, "
          + "coursename VARCHAR(50),"
          + "thumbnailpath VARCHAR(300), "
          + "primary key (id)) ";

      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.followedtutors ("
          + "username VARCHAR(20), "
          + "tutorname VARCHAR(20)) ";

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
  //@AfterAll
  public static void cleanUp() {
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


  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  //@Test
  public void createAccount() {
    String username = "usernametest";
    String email = "someemailtest@test.com";
    String hashpw = "passwordtest";
    int tutorStatus = 1;
    // Checking Account doesn't exist
    assertFalse(db.usernameExists(username));
    db.createAccount(username, email, hashpw, tutorStatus);
    assertTrue(db.usernameExists(username));
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  //@Test
  public void updateDetails() {
    String username = "usernametest";
    String hashpw = "newpasswordtest";
    assertFalse(db.checkUserDetails(username, hashpw));
    db.updateDetails(AccountDetailsUpdate.PASSWORD, hashpw);
    assertTrue(db.checkUserDetails(username, hashpw));
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  //@Test
  public void removeAccount() {
    String username = "usernametest";
    //assertTrue(db.getUserDetails(username));
    db.removeAccount(username);
    assertFalse(db.usernameExists(username));
  }

}
