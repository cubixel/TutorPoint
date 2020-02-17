import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sql.MySQL;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientHandlerTest {
    private static ClientHandler clientHandler;

    @Mock
    private MySQL mySQLMock;

    @Mock
    private ServerSocket serverSocketMock;

    @Mock
    private Socket socketMock;

    @Mock
    private DataOutputStream dosMock;

    @Mock
    private DataInputStream disMock;

    @BeforeEach
    public void mockInit() throws Exception {
        initMocks(this);

        try {
            // Then mock it
            when(serverSocketMock.accept()).thenReturn(socketMock);
            dosMock = new DataOutputStream(socketMock.getOutputStream());
            disMock = new DataInputStream(socketMock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Should not reach here");
        }

        clientHandler = new ClientHandler(socketMock, disMock, dosMock, 1, mySQLMock);
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
