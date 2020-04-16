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

public class DataServerTest {

  private int port;
  private Socket socket1;
  private DataInputStream dis1;
  private DataOutputStream dos1;
  private Socket socket2;
  private DataInputStream dis2;
  private DataOutputStream dos2;
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
    socket1.close();
    dis1.close();
    dos1.close();
    socket2.close();
    dis2.close();
    dos2.close();
  }

  @Test
  public void addClientHandlerTest() throws IOException, InterruptedException {
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
    int dataServerToken = dis2.readInt();


    assertEquals(token, dataServerToken);
  }

  @Test
  public void addTwoClientHandlersTest() throws IOException, InterruptedException {
    //add one client1
    socket1 = new Socket("localhost", port);
    dis1 = new DataInputStream(socket1.getInputStream());
    dos1 = new DataOutputStream(socket1.getOutputStream());

    //read client 1 token
    final int token1 = dis1.readInt();

    Thread.sleep(100);
    socket2 = new Socket("localhost", port + 1);
    dis2 = new DataInputStream(socket2.getInputStream());
    dos2 = new DataOutputStream(socket2.getOutputStream());
    //send client1 token
    dos2.writeInt(token1);

    // This is needed to allow the MainServer to catch up.
    Thread.sleep(100);
    //read client1 dataserver token
    final int dataServerToken1 = dis2.readInt();
    System.out.println("Token1: " + token1);
    System.out.println("DataServerToken1 : " + dataServerToken1);

    //add second client3
    Socket socket3 = new Socket("localhost", port);
    DataInputStream dis3 = new DataInputStream(socket3.getInputStream());
    final DataOutputStream dos3 = new DataOutputStream(socket3.getOutputStream());

    //read client3 token
    final int token3 = dis3.readInt();

    Thread.sleep(100);

    Socket socket4 = new Socket("localhost", port + 1);
    DataInputStream dis4 = new DataInputStream(socket4.getInputStream());
    final DataOutputStream dos4 = new DataOutputStream(socket4.getOutputStream());
    //send client3 token
    dos4.writeInt(token3);

    // This is needed to allow the MainServer to catch up.
    Thread.sleep(100);
    //read client3 token
    int dataServerToken3 = dis4.readInt();

    System.out.println("Token3: " + token3);
    System.out.println("DataServerToken3: " + dataServerToken3);


    assertEquals(token1, dataServerToken1);
    assertEquals(token3, dataServerToken3);

    //close sockets
    socket3.close();
    dis3.close();
    dos3.close();
    socket4.close();
    dis4.close();
    dos4.close();
  }

  @Test
  public void addTwoClientHandlersSameTimeTest() throws IOException, InterruptedException {
    //add both clients to mainserver
    socket1 = new Socket("localhost", port);
    dis1 = new DataInputStream(socket1.getInputStream());
    dos1 = new DataOutputStream(socket1.getOutputStream());
    Socket socket3 = new Socket("localhost", port);
    DataInputStream dis3 = new DataInputStream(socket3.getInputStream());
    final DataOutputStream dos3 = new DataOutputStream(socket3.getOutputStream());

    //read main server tokens token
    final int token1 = dis1.readInt();
    final int token3 = dis3.readInt();

    //catch up
    Thread.sleep(100);

    //add both clients to data server
    socket2 = new Socket("localhost", port + 1);
    dis2 = new DataInputStream(socket2.getInputStream());
    dos2 = new DataOutputStream(socket2.getOutputStream());
    Socket socket4 = new Socket("localhost", port + 1);
    final DataInputStream dis4 = new DataInputStream(socket4.getInputStream());
    final DataOutputStream dos4 = new DataOutputStream(socket4.getOutputStream());

    //send tokens token
    dos2.writeInt(token1);
    dos4.writeInt(token3);

    // This is needed to allow the MainServer to catch up.
    Thread.sleep(100);

    //read dataserver tokens
    final int dataServerToken1 = dis2.readInt();
    int dataServerToken3 = dis4.readInt();

    System.out.println("Token1: " + token1);
    System.out.println("DataServerToken1 : " + dataServerToken1);
    System.out.println("Token3: " + token3);
    System.out.println("DataServerToken3: " + dataServerToken3);

    assertEquals(token1, dataServerToken1);
    assertEquals(token3, dataServerToken3);

    //close sockets
    socket3.close();
    dis3.close();
    dos3.close();
    socket4.close();
    dis4.close();
    dos4.close();
  }
}
