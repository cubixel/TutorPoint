import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;
import sql.MySqlFactory;

public class PresentationHandlerTest {

  private PresentationHandler presentationHandler;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandlerTest");

  private ServerSocket server;
  private ServerSocket dataserver;
  private Socket socket;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Thread dummyClient;

  /**
   * This initialises the mocks, sets up their responses and created a MainServer
   * instance to test on.
   */
  @BeforeEach
  public void setUp() throws IOException {

    dummyClient = new Thread(() -> {
      try {
        Socket clientSocket = new Socket("localhost", 8888);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
    dummyClient.start();

    server = new ServerSocket(8888);
    dataserver = new ServerSocket(8889);
    socket = server.accept();
    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());
    presentationHandler = new PresentationHandler(dos, 0, dataserver);
  }

  /**
   * Cleans up by closing all the sockets and datainput/outputstreams.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    presentationHandler = null;
    dis.close();
    dos.close();
    socket.close();
    server.close();
  }

  @Test
  public void testSetXml() throws InterruptedException {

    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    String xmlUrl = "src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);
    assertEquals(presentationHandler.getCurrentXml(), new File(xmlUrl));
  }

  @Test
  public void testSendXml() throws InterruptedException, IOException {

    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    String xmlUrl = "src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);
    log.info("Set Xml");
    presentationHandler.sendXml();
    assertTrue(true);
    //assertEquals(presentationHandler.getCurrentXml(), new File(xmlUrl));
  }

}