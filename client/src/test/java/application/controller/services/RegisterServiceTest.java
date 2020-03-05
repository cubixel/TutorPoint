package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.Account;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


public class RegisterServiceTest {

  private RegisterService registerService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private Account accountMock;

  /**
   * METHOD DESCRIPTION.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }


  /**
   * METHOD DESCRIPTION.
   *
   * @throws Exception DESCRIPTION
   */
  @BeforeEach
  public void setUp() throws Exception {
    initMocks(this);

    when(mainConnectionMock.listenForString()).thenReturn(returnedString);

    registerService = new RegisterService(accountMock, mainConnectionMock);
  }

  @Test
  public void successfulResultTest() {
    returnedString = String.valueOf(AccountRegisterResult.SUCCESS);

    Platform.runLater(() -> {
      registerService.start();
      registerService.setOnSucceeded(event -> {
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    returnedString = String.valueOf(AccountRegisterResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      registerService.start();
      registerService.setOnSucceeded(event -> {
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_NETWORK, result);
      });
    });
  }

  @Test
  public void unexpectedErrorResultTest() {
    returnedString = String.valueOf(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR);

    Platform.runLater(() -> {
      registerService.start();
      registerService.setOnSucceeded(event -> {
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR, result);
      });
    });
  }

}
