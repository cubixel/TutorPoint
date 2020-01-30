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
public class MainTest {
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
        assertEquals(input, server.readString());
    }

    public void registerNewUser() throws IOException, ClassNotFoundException {
        String username = "New User";
        String password = "pleaseencryptthis";
        int tutorStatus = 1;
        Register newUser = new Register(username, password, tutorStatus);
        MainConnection connection = new MainConnection(null, 5000);
        connection.sendObject(newUser);
        server.readObjectStream();
        //assertTrue(connection.readString());
    }
}
