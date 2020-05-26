package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  protected Boolean isHost;
  protected Button prevSlideButton;
  protected Button nextSlideButton;
  protected Button loadPresentationButton;
  protected TextField urlBox;
  protected TextField messageBox;
  protected StackPane pane;
  protected GridPane presentationGrid;
  protected Pane controlPane;

  protected PresentationWindowController presentationWindowController;

  private static final Logger log = LoggerFactory.getLogger("PresentationWindowControllerTest");

  public void testOne() {

  }
}
