package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class LoginServiceTest {

  private LoginService loginService;

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

    loginService = new LoginService(accountMock, mainConnectionMock);
  }

  /**
   * METHOD DESCRIPTION.
   *
   * @throws IOException DESCRIPTION
   */
  @AfterEach
  public void cleanUp() throws IOException {

  }

  @Test
  public void successfulResultTest() {
    returnedString = String.valueOf(AccountLoginResult.SUCCESS);

    Platform.runLater(() -> {
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    returnedString = String.valueOf(AccountLoginResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.FAILED_BY_NETWORK, result);
      });
    });
  }

  @Test
  public void unexpectedErrorResultTest() {
    returnedString = String.valueOf(AccountLoginResult.FAILED_BY_UNEXPECTED_ERROR);

    Platform.runLater(() -> {
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.FAILED_BY_UNEXPECTED_ERROR, result);
      });
    });
  }

}