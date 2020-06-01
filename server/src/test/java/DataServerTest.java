import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the test class for the DataServer.
 *
 * @author James Gardner
 * @author Eric Walker
 * @see DataServer
 */
public class DataServerTest {

  DataServer dataServer;
  private final int port = 5124;
  private Socket socket;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Socket socket2;
  private DataInputStream dis2;
  private DataOutputStream dos2;

  private static final Logger log = LoggerFactory.getLogger("DataServerTest");

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> allClientsMock;

  @Mock
  private ClientHandler clientHandlerOneMock;

  @Mock
  private ClientHandler clientHandlerTwoMock;

  @Mock
  private MainServer mainServerMock;

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance to test
   * on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    try {
      dataServer = new DataServer(port, mainServerMock);
      dataServer.start();
    } catch (IOException e) {
      log.error("Could not setup test DataServer", e);
    }
  }

  /**
   * Cleans up by closing all the sockets and datainput/outputstreams.
   *
   * @throws IOException Socket, dis and dos will always exist.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    socket.close();
    dis.close();
    dos.close();
    socket2.close();
    dis2.close();
    dos2.close();
  }

  @Test
  public void dataServerClientNotifierCreationTest() {
    try {
      when(mainServerMock.getAllClients()).thenReturn(allClientsMock);
      when(allClientsMock.get(0)).thenReturn(clientHandlerOneMock);
      socket = new Socket("localhost", port);
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());

      // This is needed to allow the DataServer to catch up.
      Thread.sleep(100);

      dos.writeInt(0);

      // This is needed to allow the DataServer to catch up.
      Thread.sleep(100);

      assertEquals(0, dis.readInt());

      when(mainServerMock.getAllClients()).thenReturn(allClientsMock);
      when(allClientsMock.get(1)).thenReturn(clientHandlerTwoMock);

      socket2 = new Socket("localhost", port);
      dis2 = new DataInputStream(socket2.getInputStream());
      dos2 = new DataOutputStream(socket2.getOutputStream());

      // This is needed to allow the MainServer to catch up.
      Thread.sleep(100);

      dos2.writeInt(1);

      // This is needed to allow the DataServer to catch up.
      Thread.sleep(100);

      assertEquals(1, dis2.readInt());

      verify(clientHandlerOneMock, times(1)).setNotifier(any());
      verify(clientHandlerTwoMock, times(1)).setNotifier(any());
    } catch (InterruptedException | IOException e) {
      fail();
    }
  }
}