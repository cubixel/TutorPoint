
/*
 * MainTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import application.controller.services.*;
import application.model.account.Account;
import javafx.application.Platform;

import org.junit.jupiter.api.*;

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
    private String email = "someEmail@cubixel.com";
    private String password = "pleaseencryptthis";
    private static Platform platform;

    @BeforeAll
    public static void setUP() throws Exception {
        /*
         * Creating a server object on which to test, this
         * is running on a remote Raspberry Pi by default an arbitrarily
         * chosen port 52673.
         *  */
        final String JBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://cubixel.ddns.net:52673/";

        //  Database credentials
        final String USER = "java";
        final String PASS = "2pWwoP6EBH5U7XpoYuKd";


        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String SQL = "CREATE DATABASE tutorpointtest";
            stmt.executeUpdate(SQL);

            SQL = "CREATE TABLE tutorpointtest.users ("+
                    "name VARCHAR(20), " +
                    "hashedpw VARCHAR(64), "+
                    "istutor CHAR(1)) ";

            stmt.executeUpdate(SQL);
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }


        server = new MainServer(5000, "tutorpointtest");
        server.start();

        /* Null for address should default to localhost on client side. */
        connection = new MainConnection(null, 5000);

        // Needed as the Register and Login Services are JavaFX Services

        platform.startup(() -> System.out.println("Toolkit initialized ..."));
        platform.setImplicitExit(false);

    }



    @AfterAll
    public static void cleanUp() {
        final String JBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://cubixel.ddns.net:52673/";

        //  Database credentials
        final String USER = "java";
        final String PASS = "2pWwoP6EBH5U7XpoYuKd";


        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String SQL = "DROP DATABASE tutorpointtest";
            stmt.executeUpdate(SQL);

            conn.close();

        }catch (SQLException | ClassNotFoundException e){
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
    public void testConnection(){
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
        } catch(Exception e) {
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
        Thread thread = new Thread(() -> platform.runLater(() -> {
                // creating a register service.
                RegisterService registerService = new RegisterService(null, connection);

                // Creating an account to be registered
                Account account = new Account(username, email, password, tutorStatus, registerOrLogin);

                // setting the account and running the service
                registerService.setAccount(account);
                registerService.start();
                System.out.println("Here1");
                registerService.setOnSucceeded(event ->{
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
        platform.runLater(new Runnable() {
            @Override
            public void run() {
                // creating a login service.
                LoginService loginService = new LoginService(null, connection);

                // Creating an account to be registered
                Account account = new Account("thisShouldFail", password);

                loginService.setAccount(account);
                loginService.start();
                loginService.setOnSucceeded(event ->{
                    AccountLoginResult result = loginService.getValue();
                    assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS, result);
                    System.out.println("Here");
                });

                // Creating an account to be registered
                account = new Account(username, password);

                loginService.setAccount(account);
                loginService.restart();
                loginService.setOnSucceeded(event ->{
                    AccountLoginResult result = loginService.getValue();
                    assertEquals(AccountLoginResult.SUCCESS, result);
                });
            }
        });
    }

}
