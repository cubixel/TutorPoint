
/*
 * MainTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import application.controller.services.AccountLoginResult;
import application.controller.services.AccountRegisterResult;
import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.model.account.Account;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * This is the top level of the global testing class for running tests on both
 * client and server simultaneously. This is done within an external testing module
 * that has access to all of the client's and server's classes.
 *
 * @author CUBIXEL
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainTopLevelTest {
  private static MainServer server = null;
  private static MainConnection connection;
  private String username = "NewUser";
  private String password = "pleaseencryptthis";
  private static Platform platform;

  /**
   * METHOD DESCRIPTION.
   */
  @BeforeAll
  public static void setUP() throws Exception {
    /*
      * Creating a server object on which to test, this
      * is running on a remote Raspberry Pi by default an arbitrarily
      * chosen port 52673.
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


    server = new MainServer(5000, "tutorpointtest");
    server.start();

    /* Null for address should default to localhost on client side. */
    connection = new MainConnection(null, 5000);

    // Needed as the Register and Login Services are JavaFX Services
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
    Platform.setImplicitExit(false);
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

    connection.stopHeartbeat();
  }


  @Test
  @Order(1)
  public void checkSocketState() {
    /* Checking that the server socket is open. */
    assertEquals(false, MainTopLevelTest.server.isSocketClosed());
  }

  @Test
  @Order(2)
  public void testConnection() {
    /* A string "Ping" should be sent from the Client side to the
      * Server and checked on the Server side to confirm it is received. */
    String input = new String("PING");

    /* Create a connection on client side and check it worked. */
    assertEquals(false, connection.isClosed());

    /* Seems there are race conditions here so need to wait for
      * the servers thread to catch up. That's why this pause is
      * here. */
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Send a string and check the server receives that string. */
    try {
      connection.sendString(input);
      String recieved = connection.listenForString();
      assertEquals(input, recieved);
    } catch (IOException e) {
      fail("IOException");
    }
  }


  @Test
  @Order(3)
  public void registerNewUser(){
    int tutorStatus = 1; // Is a tutor
    int registerOrLogin = 1; // Is register

    // Needs to be in here as outside of JavaFX main thread
    // then we put this in a new thread as it wasn't running the code within runLater.
    Thread thread = new Thread(() -> Platform.runLater(() -> {
      // creating a register service.
      RegisterService registerService = new RegisterService(null, connection);

      // Creating an account to be registered
      Account account = new Account(username, password, tutorStatus, registerOrLogin);

      // setting the account and running the service
      registerService.setAccount(account);
      registerService.start();
      System.out.println("Here1");
      registerService.setOnSucceeded(event -> {
        AccountRegisterResult result = registerService.getValue();
        assertEquals(result, AccountRegisterResult.SUCCESS);
      });
    }));
    thread.start();
  }


  @Test
  @Order(4)
  public void loginUser(){
    // Haven't put this in a new thread yet. Doesn't run the code within runLater
    Platform.runLater(new Runnable() {
          @Override
          public void run() {
            // creating a login service.
            LoginService loginService = new LoginService(null, connection);

            // Creating an account to be registered
            Account account = new Account("thisShouldFail", password);

            loginService.setAccount(account);
            loginService.start();
            loginService.setOnSucceeded(event -> {
              AccountLoginResult result = loginService.getValue();
              assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS, result);
              System.out.println("Here");
            });

            // Creating an account to be registered
            account = new Account(username, password);

            loginService.setAccount(account);
            loginService.restart();
            loginService.setOnSucceeded(event -> {
              AccountLoginResult result = loginService.getValue();
              assertEquals(AccountLoginResult.SUCCESS, result);
            });
          }
      });
  }
}
