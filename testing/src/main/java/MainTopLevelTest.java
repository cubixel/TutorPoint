/*
 * MainTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
import application.Main;
import application.connection.MainConnection;

import application.security.Register;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * This is the top level of the global testing class for running tests on both
 * client and server simultaneously. This is an within an external testing module
 * that has access to all of client and server's classes.
 *
 * @author CUBIXEL
 *
 */
public class MainTopLevelTest {
    private static MainServer server = null;

    @BeforeClass
    public static void createServer() throws Exception {
        /*
         * Creating a server object on which to test, this
         * is running on localhost by default an arbitrarily
         * chosen port 5000.
         *  */
        server = new MainServer(5000);
        server.start();
    }

    @Test
    public void checkSocketState() throws Exception {
        /* Checking that the server socket is open. */
        assertEquals(false, this.server.isSocketClosed());
    }

    @Test
    public void createConnection() throws Exception{
        /* A string "Hello World" should be sent from the Client side to the
         * Server and checked on the Server side to confirm it is received. */
        String input = new String("Hello World");

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
        assertEquals(input, server.getClientHandler().readString());
    }

    //@Test
    public void registerNewUser() throws IOException, ClassNotFoundException {
        String username = "New User";
        String password = "pleaseencryptthis";
        int tutorStatus = 1; // Is a tutor
        Register newUser = new Register(username, password, tutorStatus);
        MainConnection connection = new MainConnection(null, 5000);
        connection.sendString(newUser.getUsername());
        connection.sendString(newUser.getHashed_pw());

        //server.createAccount();


        //assertEquals(username, server.readString());
        //assertEquals("af35e9fb4eee2f01a52893ff328d9ad7bde1bcaba06a26d6a4b86226b4624ade", server.readString());

        //connection.sendObject(newUser);
        //server.readObjectStream();
        //assertTrue(connection.readString());
    }
}
