package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Test class for the LoginService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author James Gardner
 * @see LoginService
 */
public class LoginServiceTest {

  private LoginService loginService;
  private String returnedString;
  volatile boolean threadDone;

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
   * This method ends the JavaFX runtime.
   */
  @AfterAll
  public static void cleanUp() {
    Platform.exit();
  }

  /**
   * Initialises Mocks, sets up Mock return values when called and creates
   * an instance of the UUT.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    loginService = new LoginService(accountMock, mainConnectionMock);

    threadDone = false;
  }

  @Test
  public void successfulResultTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountLoginResult.LOGIN_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      loginService.reset();
      loginService.start();

      loginService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForAccount();
        } catch (IOException e) {
          fail();
        }
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.LOGIN_SUCCESS, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  @Test
  public void failedByUserCredentialsTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountLoginResult.FAILED_BY_CREDENTIALS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      loginService.reset();
      loginService.start();

      loginService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForAccount();
        } catch (IOException e) {
          fail();
        }
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.FAILED_BY_CREDENTIALS, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  @Test
  public void failedByNetworkTest() {
    // Setting Mock return value.
    try {
      when(mainConnectionMock.listenForString()).thenThrow(IOException.class);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      loginService.reset();
      loginService.start();

      loginService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForAccount();
        } catch (IOException e) {
          fail();
        }
        AccountLoginResult result = loginService.getValue();

        assertEquals(AccountLoginResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}