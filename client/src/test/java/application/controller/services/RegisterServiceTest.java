package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountRegisterResult;
import application.model.Account;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  private RegisterService registerService;
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
   * Initialises Mocks, sets up Mock return values when called and creates
   * an instance of the UUT.
   */
  @BeforeEach
  public void setUp() {
    initMocks(this);

    registerService = new RegisterService(accountMock, mainConnectionMock);

    threadDone = false;
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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
