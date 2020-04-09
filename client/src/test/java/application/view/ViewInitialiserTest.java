package application.view;

import static org.junit.jupiter.api.Assertions.assertThrows;

import application.controller.BaseController;
import application.controller.LoginWindowController;
import application.controller.services.MainConnection;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ViewInitialiserTest {


  @Mock
  private ViewFactory viewFactoryMock;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private Stage stageMock;

  private ViewInitialiser viewInitialiser;

  @BeforeEach
  public void setUp() {
    viewInitialiser = new ViewInitialiser();
  }

  @Test()
  public void testInitializeStage() {
    BaseController loginWindowController
        = new LoginWindowController(viewFactoryMock, "somePath.fxml", mainConnectionMock);
    assertThrows(IllegalStateException.class, () -> {
      viewInitialiser.initialiseStage(loginWindowController, stageMock);
    });
  }
}
