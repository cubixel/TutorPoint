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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import services.AccountDetailsUpdate;

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
   * METHOD DESCRIPTION.
   */
  @BeforeAll
  public static void createServer() throws Exception {
    /*
      * Creating a server object on which to test, this
      * is running on localhost by default an arbitrarily
      * chosen port 5000.
      *  */
    final String jbc_driver = "com.mysql.cj.jdbc.Driver";
    final String db_url = "jdbc:mysql://cubixel.ddns.net:52673/";

    //  Database credentials
    final String user = "java";
    final String pass = "2pWwoP6EBH5U7XpoYuKd";


    Connection conn = null;
    Statement stmt = null;

    try {
      Class.forName(jbc_driver);

      conn = DriverManager.getConnection(db_url, user, pass);

      stmt = conn.createStatement();
      String sql = "CREATE DATABASE tutorpointtest";
      stmt.executeUpdate(sql);

      sql = "CREATE TABLE tutorpointtest.users ("
              + "name VARCHAR(20), "
              + "hashedpw VARCHAR(64), "
              + "istutor CHAR(1)) ";

      stmt.executeUpdate(sql);
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    db = new MySql("tutorpointtest");
  }

  /**
   * METHOD DESCRIPTION.
   */
  @AfterAll
  public static void cleanUp() {
    final String jbc_driver = "com.mysql.cj.jdbc.Driver";
    final String db_url = "jdbc:mysql://cubixel.ddns.net:52673/";

    //  Database credentials
    final String user = "java";
    final String pass = "2pWwoP6EBH5U7XpoYuKd";


    Connection conn = null;
    Statement stmt = null;

    try {
      Class.forName(jbc_driver);

      conn = DriverManager.getConnection(db_url, user, pass);

      stmt = conn.createStatement();
      String sql = "DROP DATABASE tutorpointtest";
      stmt.executeUpdate(sql);

      conn.close();

    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void createAccount() {
    String username = "usernametest";
    String hashpw = "passwordtest";
    int tutorStatus = 1;
    // Checking Account doesn't exist
    assertFalse(db.getUserDetails(username));
    db.createAccount(username, hashpw, tutorStatus);
    assertTrue(db.getUserDetails(username));
  }


  @Test
  @Disabled
  public void updateDetails() {
    String username = "usernametest";
    String hashpw = "newpasswordtest";
    assertFalse(db.checkUserDetails(username, hashpw));
    db.updateDetails(AccountDetailsUpdate.PASSWORD, hashpw);
    assertTrue(db.checkUserDetails(username, hashpw));

  }

  @Test
  public void removeAccount() throws SQLException {
    String username = "usernametest";
    //assertTrue(db.getUserDetails(username));
    db.removeAccount(username);
    assertFalse(db.getUserDetails(username));
  }
}
