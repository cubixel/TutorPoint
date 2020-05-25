package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the MainWindowController. It tests
 * initialising the main window as both a student
 * and a tutor.
 *
 * @author James Gardner
 * @see MainWindowController
 */
public class MainWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  protected SubjectManager subjectManager;
  protected TutorManager tutorManager;
  protected Account account;
  protected TabPane navbar;
  protected AnchorPane homeWindow;
  protected AnchorPane subscriptionsWindow;
  protected AnchorPane discoverWindow;
  protected AnchorPane profileWindow;
  protected AnchorPane streamWindow;
  protected ButtonBar buttonbar;
  protected Button logOutButton;

  protected MainWindowController mainWindowController;

  private static final Logger log = LoggerFactory.getLogger("MainWindowControllerTest");

  /**
   * This is testing initialiseNavBarStudent.
   */
  public void initialiseNavBarStudentTest() {
    assertEquals(5, navbar.getTabs().size());
    mainWindowController.initialize(null, null);
    assertEquals(4, navbar.getTabs().size());
    try {
      verify(viewFactoryMock, times(1))
          .embedHomeWindow(homeWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedDiscoverWindow(discoverWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedProfileWindow(profileWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedSubscriptionsWindow(subscriptionsWindow, mainWindowController);
    } catch (IOException e) {
      log.error("Did not embed windows on initialisation");
      fail(e);
    }
  }

  /**
   * This is testing initialiseNavBarTutor.
   */
  public void initialiseNavBarTutorTest() {
    account.setTutorStatus(1);
    assertEquals(5, navbar.getTabs().size());
    mainWindowController.initialize(null, null);
    assertEquals(5, navbar.getTabs().size());
    try {
      verify(viewFactoryMock, times(1))
          .embedHomeWindow(homeWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedDiscoverWindow(discoverWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedProfileWindow(profileWindow, mainWindowController);
      verify(viewFactoryMock, times(1))
          .embedSubscriptionsWindow(subscriptionsWindow, mainWindowController);
    } catch (IOException e) {
      log.error("Did not embed windows on initialisation");
      fail(e);
    }
  }
}
