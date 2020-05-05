import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationHandlerTest {

  private PresentationHandler presentationHandler;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandlerTest");

  @Mock
  private Socket socket;

  @Mock
  private DataInputStream dis;

  @Mock
  private DataOutputStream dos;

  @Mock
  private ClientHandler parent;

  @Mock
  private Session session;

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    /*port = 5000;
    try {
      socket = new Socket("localhost", port);
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      //pass
    }
    */

    initMocks(this);

    
  }

  /**
   * Cleans up by closing all the sockets and datainput/outputstreams.
   * @throws IOException Socket, dis and dos will always exist.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    /*socket.close();
    dis.close();
    dos.close();*/
  }

  @Test
  public void testSetXml() throws InterruptedException {
    presentationHandler = new PresentationHandler(session);
    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    String xmlUrl = "server/src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);
    assertEquals(presentationHandler.getCurrentXml(), new File(xmlUrl));
  }

  @Test
  public void testSendXml() throws InterruptedException {
    presentationHandler = new PresentationHandler(session);
    presentationHandler.start();
    // This is needed to allow the PresentationHandler to catch up.
    initMocks(this);
    Thread.sleep(100);
    String xmlUrl = "src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);
    log.info("Set Xml");

    presentationHandler.setRequestAction("sendXml");

    Thread.sleep(10000);

    presentationHandler.exit();

        
    //assertEquals(presentationHandler.getCurrentXml(), new File(xmlUrl));
  }

}