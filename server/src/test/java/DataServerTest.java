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
import org.slf4j.Logger;
import sql.MySql;
import sql.MySqlFactory;

public class DataServerTest {

  private int port;
  private Socket socket1;
  private DataInputStream dis1;
  private DataOutputStream dos1;
  private Socket socket2;
  private DataInputStream dis2;
  private DataOutputStream dos2;
  private MainServer mainServer;
  private DataServer dataServer;

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
    socket1.close();
    dis1.close();
    dos1.close();
    socket2.close();
    dis2.close();
    dos2.close();
  }

  @Test
  public void clientHandlerCreatedTest() throws IOException, InterruptedException {
    socket1 = new Socket("localhost", port);
    dis1 = new DataInputStream(socket1.getInputStream());
    dos1 = new DataOutputStream(socket1.getOutputStream());

    final int token = dis1.readInt();

    Thread.sleep(100);
    socket2 = new Socket("localhost", port + 1);
    dis2 = new DataInputStream(socket2.getInputStream());
    dos2 = new DataOutputStream(socket2.getOutputStream());
    dos2.writeInt(token);

    // This is needed to allow the MainServer to catch up.
    Thread.sleep(100);
    int dataServerToken = Integer.parseInt(dis2.readUTF());


    assertEquals(token, dataServerToken);
  }
}
