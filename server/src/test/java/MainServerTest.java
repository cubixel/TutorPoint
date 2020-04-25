import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sql.MySql;
import sql.MySqlFactory;

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

    mainServer = new MainServer(port, mySqlFactoryMock, databaseName);
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
  public void clientHandlerCreatedTest() throws IOException, InterruptedException {
    socket = new Socket("localhost", port);
    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());

    // This is needed to allow the MainServer to catch up.
    Thread.sleep(100);

    assertEquals(1, mainServer.getAllClients().size());
  }
}
