import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import sql.MySQL;

public class ClientHandlerTest {
    private ClientHandler clientHandler;

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
            when(disMock.readUTF()).thenReturn(null).thenReturn("Account");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Should not reach here");
        }

        this.clientHandler = new ClientHandler(socketMock, disMock, dosMock, 1, mySQLMock);
    }

    @Test
    public void logOutTest(){
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
