import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;

/**
 * This is testing the PresentationHandlerTest class. It updates
 * the setRequestAction with new Strings to test the actions
 * are handled correctly.
 *
 * @author Eric Walker
 * @author Daniel Bishop
 * @author James Gardner
 *
 * @see PresentationHandler
 */
public class PresentationHandlerTest {

  @Mock
  private ClientHandler clientHandlerMock;

  @Mock
  private Session sessionMock;

  @Mock
  private ConcurrentHashMap<Integer, ClientHandler> sessionUsersMock;

  @Mock
  private ClientNotifier clientNotifierMock;

  private PresentationHandler presentationHandler;

  private static final Logger log = LoggerFactory.getLogger("PresentationHandlerTest");

  /**
   * This initialises the mocks, sets up their responses and created a MainServer instance
   * to test on.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    presentationHandler = new PresentationHandler(sessionMock);
  }


  @Test
  public void setXmlTest() throws InterruptedException {
    presentationHandler.start();
    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    String xmlUrl = "server/src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);
    assertEquals(presentationHandler.getCurrentXml(), new File(xmlUrl));
  }

  @Test
  public void uploadXmlTest() throws InterruptedException {
    presentationHandler.start();
    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    String xmlUrl = "src/main/resources/presentations/XmlTestSetting.xml";
    presentationHandler.setXml(xmlUrl);

    when(sessionMock.getThisHandler()).thenReturn(clientHandlerMock);
    when(sessionMock.getSessionUsers()).thenReturn(sessionUsersMock);
    when(clientHandlerMock.getNotifier()).thenReturn(clientNotifierMock);

    presentationHandler.setRequestAction("uploadXml");

    Thread.sleep(1000);

    try {
      verify(clientNotifierMock, times(1)).listenForFile(any());
    } catch (IOException e) {
      log.error("Failed to mock ClientNotifier");
      fail(e);
    }

    presentationHandler.exit();
  }

  @Test
  public void changeSlideNumTest() throws InterruptedException {
    presentationHandler.start();
    // This is needed to allow the PresentationHandler to catch up.
    Thread.sleep(100);
    when(sessionMock.getSessionUsers()).thenReturn(sessionUsersMock);
    when(clientHandlerMock.getNotifier()).thenReturn(clientNotifierMock);

    assertEquals(-1, presentationHandler.getSlideNum());

    presentationHandler.setRequestSlideNum(2);
    presentationHandler.setRequestAction("changeSlide");

    Thread.sleep(1000);

    assertEquals(2, presentationHandler.getSlideNum());

    presentationHandler.exit();
  }
}