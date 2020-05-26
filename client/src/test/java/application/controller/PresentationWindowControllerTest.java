package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.controller.presentation.PresentationObject;
import application.controller.presentation.PresentationObjectFactory;
import application.controller.presentation.TimingManager;
import application.controller.presentation.TimingManagerFactory;
import application.controller.presentation.XmlHandler;
import application.controller.presentation.XmlHandlerFactory;
import application.controller.presentation.exceptions.PresentationCreationException;
import application.controller.presentation.exceptions.XmlLoadingException;
import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class PresentationWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected ListenerThread listenerThreadMock;

  @Mock
  protected TimingManager timingManagerMock;

  @Mock
  protected TimingManagerFactory timingManagerFactoryMock;

  @Mock
  protected XmlHandlerFactory xmlHandlerFactoryMock;

  @Mock
  protected XmlHandler xmlHandlerMock;

  @Mock
  protected PresentationObjectFactory presentationObjectFactoryMock;

  @Mock
  protected PresentationObject presentationObjectMock;

  @Mock
  protected File fileMock;

  @Mock
  protected Document documentMock;

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

  /**
   * This is testing initialiseAsTutor.
   */
  public void initialiseAsTutorTest() {
    when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
    when(timingManagerFactoryMock.createTimingManager(any(), any())).thenReturn(timingManagerMock);

    presentationWindowController = new PresentationWindowController(viewFactoryMock, null,
        mainConnectionMock, true, prevSlideButton, nextSlideButton, loadPresentationButton,
        urlBox, messageBox, pane, presentationGrid, controlPane, timingManagerFactoryMock,
        xmlHandlerFactoryMock, presentationObjectFactoryMock);

    presentationWindowController.initialize(null, null);

    assertEquals(1, presentationGrid.getChildren().size());
    verify(listenerThreadMock, times(1))
        .addPresentationWindowController(presentationWindowController);
  }

  /**
   * This is testing initialiseAsUserTest.
   */
  public void initialiseAsUserTest() {
    when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
    when(timingManagerFactoryMock.createTimingManager(any(), any())).thenReturn(timingManagerMock);

    presentationWindowController = new PresentationWindowController(viewFactoryMock, null,
        mainConnectionMock, false, prevSlideButton, nextSlideButton, loadPresentationButton,
        urlBox, messageBox, pane, presentationGrid, controlPane, timingManagerFactoryMock,
        xmlHandlerFactoryMock, presentationObjectFactoryMock);

    presentationWindowController.initialize(null, null);

    assertEquals(0, presentationGrid.getChildren().size());
    verify(listenerThreadMock, times(1))
        .addPresentationWindowController(presentationWindowController);
  }

  /**
   * This is testing initialiseAsUserTest.
   */
  public void slideChangeTest() {
    when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
    when(timingManagerFactoryMock.createTimingManager(any(), any())).thenReturn(timingManagerMock);

    presentationWindowController = new PresentationWindowController(viewFactoryMock, null,
        mainConnectionMock, true, prevSlideButton, nextSlideButton, loadPresentationButton,
        urlBox, messageBox, pane, presentationGrid, controlPane, timingManagerFactoryMock,
        xmlHandlerFactoryMock, presentationObjectFactoryMock);

    presentationWindowController.initialize(null, null);

    when(timingManagerMock.getSlideNumber()).thenReturn(0);

    presentationWindowController.nextSlide();

    verify(timingManagerMock, times(1)).changeSlideTo(1);

    when(timingManagerMock.getSlideNumber()).thenReturn(1);

    presentationWindowController.prevSlide();

    verify(timingManagerMock, times(1)).changeSlideTo(0);

    try {
      verify(mainConnectionMock, times(2)).sendString(any());
    } catch (IOException e) {
      log.error("Failed to verify MainConnectionMock");
      fail(e);
    }
  }

  /**
   * This is testing the displayFile method.
   */
  public void displayFileTest() {
    when(mainConnectionMock.getListener()).thenReturn(listenerThreadMock);
    when(timingManagerFactoryMock.createTimingManager(any(), any())).thenReturn(timingManagerMock);

    presentationWindowController = new PresentationWindowController(viewFactoryMock, null,
        mainConnectionMock, true, prevSlideButton, nextSlideButton, loadPresentationButton,
        urlBox, messageBox, pane, presentationGrid, controlPane, timingManagerFactoryMock,
        xmlHandlerFactoryMock, presentationObjectFactoryMock);

    presentationWindowController.initialize(null, null);

    try {
      when(xmlHandlerFactoryMock.createXmlHandler()).thenReturn(xmlHandlerMock);
      when(xmlHandlerMock.makeXmlFromUrl(any())).thenReturn(documentMock);
      when(presentationObjectFactoryMock
          .createPresentationObject(any())).thenReturn(presentationObjectMock);
      when(presentationObjectMock.getDfSlideWidth()).thenReturn(0);
      when(presentationObjectMock.getDfSlideHeight()).thenReturn(0);
    } catch (XmlLoadingException | PresentationCreationException e) {
      log.error("Failed to setup mock return values");
      fail(e);
    }

    Platform.runLater(() -> {
      presentationWindowController.displayFile(fileMock, 1);
    });

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      log.error("Failed to sleep thread");
      fail(e);
    }

    verify(timingManagerMock, times(1)).start();
    verify(timingManagerMock, times(1)).changeSlideTo(1);
    assertEquals("Finished Loading", messageBox.getText());
  }
}
