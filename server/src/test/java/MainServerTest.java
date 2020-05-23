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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;
import sql.MySqlFactory;

/**
 * Test class for the MainServer, tests that the
 * MainServer is listening to the port it is set on and
 * that it is handling client connections correctly.
 *
 * @author James Gardner
 */
@TestInstance(Lifecycle.PER_CLASS)
public class MainServerTest {

  private final int port = 5000;
  private Socket socket;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Socket socket2;
  private DataInputStream dis2;
  private DataOutputStream dos2;
  private MainServer mainServer;

  private static final Logger log = LoggerFactory.getLogger("MainServerTest");

  @Mock
  private MySql mySqlMock;

  @Mock
  private MySqlFactory mySqlFactoryMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> allClientsMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> loggedInClientsMock;

  @Mock
  private DataServer dataServerMock;

  /**
   * Creates the instance of MainServer to test on.
   */
  @BeforeAll
  public void createMainServer() {
    initMocks(this);
    String databaseName = "testdb";
    mainServer = new MainServer(port, mySqlFactoryMock, databaseName,
        allClientsMock, loggedInClientsMock, dataServerMock);
    mainServer.start();
  }

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    try {
      when(mySqlFactoryMock.createConnection()).thenReturn(mySqlMock);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void multipleClientHandlersCreatedTest() {
    try {
      socket = new Socket("localhost", port);
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());

      // This is needed to allow the MainServer to catch up.
      Thread.sleep(100);

      assertEquals(0, dis.readInt());

      socket2 = new Socket("localhost", port);
      dis2 = new DataInputStream(socket2.getInputStream());
      dos2 = new DataOutputStream(socket2.getOutputStream());

      // This is needed to allow the MainServer to catch up.
      Thread.sleep(100);

      assertEquals(1, dis2.readInt());

      verify(mySqlFactoryMock, times(2)).createConnection();

      assertEquals(2, mainServer.getAllClients().size());
    } catch (IOException | InterruptedException | SQLException e) {
      fail(e);
    }

    try {
      socket.close();
      dis.close();
      dos.close();
      socket2.close();
      dis2.close();
      dos2.close();
    } catch (IOException e) {
      log.error("Could not close Sockets and DataI/OStreams", e);
    }
  }
}