package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

public class LoginServiceTest {

  private LoginService loginService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private Account accountMock;


  /**
   * Sets up the JavaFX Toolkit for running JavaFX processes on.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }


  /**
   * Initialises Mocks, sets up Mock return values when called and creates
   * an instance of the UUT.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    try {
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    loginService = new LoginService(accountMock, mainConnectionMock);
  }

  @Test
  public void successfulResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(AccountLoginResult.LOGIN_SUCCESS);

    Platform.runLater(() -> {
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.LOGIN_SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    // Setting Mock return value.
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
    // Setting Mock return value.
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