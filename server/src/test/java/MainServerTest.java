import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sql.MySql;
import sql.MySqlFactory;

/**
 * Test class for the MainServer, tests that the
 * MainServer is listening to the port it is set on and
 * that it is handling client connections correctly.
 *
 * @author James Gardner
 */
public class MainServerTest {

  private int port;
  private Socket socket;
  private DataInputStream dis;
  private DataOutputStream dos;
  private MainServer mainServer;

  @Mock
  private MySql mySqlMock;

  @Mock
  private MySqlFactory mySqlFactoryMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> allClientsMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> loggedInClientsMock;

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);
    try {
      when(mySqlFactoryMock.createConnection()).thenReturn(mySqlMock);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    port = 5000;
    String databaseName = "testdb";

    mainServer = new MainServer(port, mySqlFactoryMock, databaseName,
        allClientsMock, loggedInClientsMock);
    mainServer.start();
  }

  /**
   * Cleans up by closing all the sockets and datainput/outputstreams.
   * @throws IOException Socket, dis and dos will always exist.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    socket.close();
    dis.close();
    dos.close();
  }

  @Test
  public void clientHandlerCreatedTest() {
    try {
      socket = new Socket("localhost", port);
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());

      // This is needed to allow the MainServer to catch up.
      Thread.sleep(100);

      verify(mySqlFactoryMock, times(1)).createConnection();

      assertEquals(1, mainServer.getAllClients().size());
    } catch (IOException | InterruptedException | SQLException e) {
      fail(e);
    }
  }
}
