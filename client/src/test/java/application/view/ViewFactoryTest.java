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
    viewFactory.showLoginWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowRegisterWindow() {
    viewFactory.showRegisterWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowMainWindow() {
    viewFactory.showMainWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowWhiteboardWindow() {
    viewFactory.showWhiteboardWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowMediaPlayerWindow() {
    viewFactory.showMediaPlayerWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowWebcamWindow() {
    viewFactory.showWebcamWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

  @Test
  public void testShowTextWindow() {
    viewFactory.showTextWindow(stageMock);
    verify(viewInitialiserMock).initialiseStage(any(), any());
  }

}
