/*
 * MySQLTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
package sql;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.AccountDetailsUpdate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS DESCRIPTION:
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySQLTest {

    private static MySQL db = null;


    //@BeforeAll
    public static void createSever(){
        db = new MySQL("tutorpoint");
    }

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

        db = new MySQL("tutorpointtest");
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
    public void createAccount() {
        String username = "qwerty";
        String hashpw = "lakdjsf";
        int tutorStatus = 1;
        // Checking Account doesn't exist
        assertFalse(db.getUserDetails(username));
        db.createAccount(username, hashpw, tutorStatus);
        assertTrue(db.getUserDetails(username));
    }


    //@Test
    public void updateDetails(){
        String username = "qwerty";
        String hashpw = "asdfasdf";
        assertFalse(db.checkUserDetails(username, hashpw));
        db.updateDetails(AccountDetailsUpdate.PASSWORD, hashpw);
        assertTrue(db.checkUserDetails(username, hashpw));

    }

    @Test
    public void removeAccount() throws SQLException {
        String username = "qwerty";
        //assertTrue(db.getUserDetails(username));
        db.removeAccount(username);
        assertFalse(db.getUserDetails(username));
    }

}
