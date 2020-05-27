package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
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

  protected LoginService loginService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected Account accountMock;

  /**
   * This is the successfulResultTest.
   */
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

  /**
   * This is the failedByUserCredentialsTest.
   */
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

  /**
   * This is the failedByNetworkTest.
   */
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