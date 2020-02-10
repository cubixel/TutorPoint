/*
 * MainTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
import application.controller.services.MainConnection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the top level of the global testing class for running tests on both
 * client and server simultaneously. This is done within an external testing module
 * that has access to all of the client's and server's classes.
 *
 * @author CUBIXEL
 *
 */
public class MainTopLevelTest {
    private static MainServer server = null;

    @BeforeAll
    public static void createServer() throws Exception {
        /*
         * Creating a server object on which to test, this
         * is running on localhost by default an arbitrarily
         * chosen port 5000.
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
    }


    @Test
    public void checkSocketState() throws Exception {
        /* Checking that the server socket is open. */
        assertEquals(false, this.server.isSocketClosed());
    }

    @Test
    public void createConnection() throws Exception{
        /* A string "Ping" should be sent from the Client side to the
         * Server and checked on the Server side to confirm it is received. */
        String input = new String("Ping");

        /* Null for address should default to localhost on client side. */
        String connection_adr = null;

        /* Create a connection on client side and check it worked. */
        MainConnection connection = new MainConnection(connection_adr, 5000);
        assertEquals(false, connection.isClosed());

        /* Send a string and check the server receives that string. */
        connection.sendString(input);

        /* Seems there are race conditions here so need to wait for
         * the servers thread to catch up. That's why this pause is
         * here. */
        Thread.sleep(1000);
        assertEquals(input, connection.listenForString());
    }

    //@Test
    public void registerNewUser() throws IOException, ClassNotFoundException {
        /*
        String username = "New User";
        String password = "pleaseencryptthis";
        int tutorStatus = 1; // Is a tutor
        Register newUser = new Register(username, password, tutorStatus);
        MainConnection connection = new MainConnection(null, 5000);
        connection.sendString(newUser.getUsername());
        connection.sendString(newUser.getHashed_pw());
        */
        //server.createAccount();


        //assertEquals(username, server.readString());
        //assertEquals("af35e9fb4eee2f01a52893ff328d9ad7bde1bcaba06a26d6a4b86226b4624ade", server.readString());

        //connection.sendObject(newUser);
        //server.readObjectStream();
        //assertTrue(connection.readString());
    }
}
