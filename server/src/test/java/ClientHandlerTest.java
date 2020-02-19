import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
            dosMock = new DataOutputStream(socketMock.getOutputStream());
            disMock = new DataInputStream(socketMock.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            fail("Should not reach here");
        }

        clientHandler = new ClientHandler(disMock, dosMock, 1, mySQLMock);
    }

    @Test
    public void loginTest() throws IOException {
        // This could also check the function of LogOut, that the client thread is stopped correctly
        when(disMock.readUTF()).thenReturn(null).thenReturn(null); //TODO Create JSON;
    }

    @Test
    public void readString() {
        /* dis should be set to some value and then check that the readString() function within the client handler
        * outputs that sring. This might have to be a higher level function tested by the Testing package so as to
        * have access to the server and client sides.*/
        assertEquals(clientHandler.readString(), "Test");

    }
}
