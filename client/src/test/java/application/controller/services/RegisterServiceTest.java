package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.controller.enums.AccountRegisterResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the RegisterService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author James Gardner
 * @see RegisterService
 */
public class RegisterServiceTest {

  protected RegisterService registerService;
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
      returnedString = String.valueOf(AccountRegisterResult.ACCOUNT_REGISTER_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      registerService.reset();
      registerService.start();

      registerService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForString();
        } catch (IOException e) {
          fail();
        }
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.ACCOUNT_REGISTER_SUCCESS, result);
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
      returnedString = String.valueOf(AccountRegisterResult.FAILED_BY_CREDENTIALS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      registerService.reset();
      registerService.start();

      registerService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForString();
        } catch (IOException e) {
          fail();
        }
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_CREDENTIALS, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByUsernameTakenTest.
   */
  public void failedByUsernameTakenTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountRegisterResult.FAILED_BY_USERNAME_TAKEN);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      registerService.reset();
      registerService.start();

      registerService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForString();
        } catch (IOException e) {
          fail();
        }
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_USERNAME_TAKEN, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByEmailTakenTest.
   */
  public void failedByEmailTakenTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountRegisterResult.FAILED_BY_EMAIL_TAKEN);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.listenForAccount()).thenReturn(accountMock);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      registerService.reset();
      registerService.start();

      registerService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForString();
        } catch (IOException e) {
          fail();
        }
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_EMAIL_TAKEN, result);
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
      registerService.reset();
      registerService.start();

      registerService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).listenForString();
        } catch (IOException e) {
          fail();
        }
        AccountRegisterResult result = registerService.getValue();

        assertEquals(AccountRegisterResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

}
