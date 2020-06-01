package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Testing the Abstract BaseController Class through its
 * constructor and its getter methods.
 *
 * @author James Gardner
 */
public class BaseControllerTest {

  @Mock
  private ViewFactory viewFactoryMock;

  @Mock
  private MainConnection mainConnectionMock;

  @Test
  public void constructorAndGetterTest() {
    String fxmlName = "TestName.fxml";
    BaseController baseController = new LoginWindowController(viewFactoryMock, fxmlName,
        mainConnectionMock);
    assertEquals(viewFactoryMock, baseController.getViewFactory());
    assertEquals(fxmlName, baseController.getFxmlName());
    assertEquals(mainConnectionMock, baseController.getMainConnection());
  }
}