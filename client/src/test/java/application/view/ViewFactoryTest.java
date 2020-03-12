package application.view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.services.MainConnection;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ViewFactoryTest {

  private ViewFactory viewFactory;

  @Mock
  private Stage stageMock;

  @Mock
  private MainConnection mainConnectionMock;
  
  @Mock
  private ViewInitialiser viewInitialiserMock;

  @BeforeEach
  public void setUp() {
    initMocks(this);
    viewFactory = new ViewFactory(mainConnectionMock, viewInitialiserMock);
  }

  @Test
  public void testShowLoginWindow() {
    viewFactory.showLoginWindow();
    verify(viewInitialiserMock).initialiseStage(any());
    assertFalse(viewFactory.isMainViewInitialised());
  }

  @Test
  public void testShowRegisterWindow() {
    viewFactory.showRegisterWindow();
    verify(viewInitialiserMock).initialiseStage(any());
    assertFalse(viewFactory.isMainViewInitialised());
  }

  @Test
  public void testShowMainWindow() {
    viewFactory.showMainWindow();
    verify(viewInitialiserMock).initialiseStage(any());
    assertTrue(viewFactory.isMainViewInitialised());
  }

  @Test
  public void testShowOptionsWindow() {
    viewFactory.showOptionsWindow();
    verify(viewInitialiserMock).initialiseStage(any());
  }

  @Test
  public void testShowWhiteboardWindow() {
    viewFactory.showWhiteboardWindow();
    verify(viewInitialiserMock).initialiseStage(any());
  }

  @Test
  public void testShowMediaPlayerWindow() {
    viewFactory.showMediaPlayerWindow();
    verify(viewInitialiserMock).initialiseStage(any());
  }

  @Test
  public void testShowWebcamWindow() {
    viewFactory.showWebcamWindow();
    verify(viewInitialiserMock).initialiseStage(any());
  }

  @Test
  public void testCloseStage() {
    viewFactory.closeStage(stageMock);
    verify(stageMock).close();
  }
}
