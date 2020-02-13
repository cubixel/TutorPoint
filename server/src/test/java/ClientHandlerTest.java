import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sql.MySQL;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientHandlerTest {
    private static ClientHandler clientHandler;
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static DataInputStream dis = null;
    private static DataOutputStream dos = null;

    @Mock
    private static MySQL mySQLMock;

    @BeforeAll
    public static void setUp() throws IOException {
        socket = new Socket();
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 5000;
        SocketAddress socketAddress=new InetSocketAddress(inetAddress, port);
        socket.bind(socketAddress);
        socket.connect(socketAddress);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        clientHandler = new ClientHandler(socket, dis, dos, 1, mySQLMock);
    }

    @BeforeEach
    public void mockInit() throws Exception {
        initMocks(this);
    }

    @Test
    public void logOutTest(){
        assertTrue(clientHandler.isLoggedIn());
        clientHandler.logOut();
        assertFalse(clientHandler.isLoggedIn());
        // This could also check the function of LogOut, that the client thread is stopped correctly
    }

    /* Functions In Need of Tests
    *
    * ReadString
    * WriteString
    * Login user
    * CreateNewUser
    *
    * */

}
