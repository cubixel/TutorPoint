package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.controller.enums.AccountUpdateResult;
import application.model.updates.AccountUpdate;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the UpdateDetailsService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author James Gardner
 * @see UpdateDetailsService
 */
public class UpdateDetailsServiceTest {

  protected UpdateDetailsService updateDetailsService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected AccountUpdate accountUpdateMock;

  /**
   * This is the successfulResultTest.
   */
  public void successfulResultTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }
    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByCredentialsTest.
   */
  public void failedByCredentialsTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(AccountUpdateResult.FAILED_BY_CREDENTIALS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_CREDENTIALS, result);
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
      returnedString = String.valueOf(AccountUpdateResult.FAILED_BY_USERNAME_TAKEN);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_USERNAME_TAKEN, result);
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
      returnedString = String.valueOf(AccountUpdateResult.FAILED_BY_EMAIL_TAKEN);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_EMAIL_TAKEN, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failingByNetworkTest.
   */
  public void failingByNetworkTest() {
    // Setting Mock return value.
    try {
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      doThrow(IOException.class).when(mainConnectionMock).sendString(any());
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}