package application.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.BaseController;
import application.controller.MainWindowController;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.managers.SubjectManager;
import java.io.IOException;
import java.util.HashMap;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for the ViewFactory verifies that the correct
 * method is called along with the correct controller.
 *
 * @author James Gardner
 */
public class ViewFactoryTest {

  private ViewFactory viewFactory;
  private static final Logger log = LoggerFactory.getLogger("ViewFactoryTest");

  @Mock
  private Stage stageMock;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private ViewInitialiser viewInitialiserMock;

  @Mock
  private HashMap<String, BaseController> windowControllersMock;

  @Mock
  private Account accountMock;

  @Mock
  private Tutor tutorMock;

  @Mock
  private SubjectManager subjectManagerMock;

  @Mock
  private Subject subjectMock;

  @Mock
  MainWindowController mainWindowControllerMock;

  @BeforeEach
  public void setUp() {
    initMocks(this);
    viewFactory = new ViewFactory(mainConnectionMock, viewInitialiserMock, windowControllersMock);
  }

  @Test
  public void showLoginWindowTest() {
    viewFactory.showLoginWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showMainWindowTest() {
    viewFactory.showMainWindow(stageMock, accountMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showRegisterWindowTest() {
    viewFactory.showRegisterWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showWhiteboardWindowTest() {
    viewFactory.showWhiteboardWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showPresentationWindowTest() {
    viewFactory.showPresentationWindow(stageMock, true);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showMediaPlayerWindowTest() {
    viewFactory.showMediaPlayerWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showWebcamWindowTest() {
    viewFactory.showWebcamWindow(stageMock, true);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void showTextChatWindowTest() {
    viewFactory.showTextChatWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void embedProfileWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedProfileWindow(anchorPane, mainWindowControllerMock);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedStreamWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedStreamWindow(anchorPane, accountMock, 1, true);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedDiscoverWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedDiscoverWindow(anchorPane, mainWindowControllerMock);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedSubjectWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    when(mainWindowControllerMock.getSubjectManager()).thenReturn(subjectManagerMock);
    when(subjectManagerMock.getSubject(1)).thenReturn(subjectMock);
    when(subjectMock.getId()).thenReturn(1);
    when(subjectMock.isFollowed()).thenReturn(true);
    try {
      viewFactory.embedSubjectWindow(anchorPane, mainWindowControllerMock, 1);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedTutorWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedTutorWindow(anchorPane, mainWindowControllerMock, tutorMock);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedWhiteboardWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedWhiteboardWindow(anchorPane, 1, 1);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedPresentationWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedPresentationWindow(anchorPane, true);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedTextChatWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedTextChatWindow(anchorPane, "TestUserName", 1, 1);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedHomeWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedHomeWindow(anchorPane, mainWindowControllerMock);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void embedSubscriptionsWindowTest() {
    AnchorPane anchorPane = new AnchorPane();
    try {
      viewFactory.embedSubscriptionsWindow(anchorPane, mainWindowControllerMock);
      verify(viewInitialiserMock).initialiseEmbeddedStage(any(), any());
    } catch (IOException e) {
      log.error("ViewInitialiserMock has Failed", e);
      fail();
    }
  }

  @Test
  public void getWindowControllersTest() {
    assertEquals(windowControllersMock, viewFactory.getWindowControllers());
  }
}