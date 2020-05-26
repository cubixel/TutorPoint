package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.controller.enums.SessionRequestResult;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the SessionRequestService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author James Gardner
 * @see SessionRequestService
 */
public class SessionRequestServiceTest {

  protected SessionRequestService sessionRequestService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  /**
   * This is the claimingConnectionTest.
   */
  public void claimingConnectionTest() {
    try {
      returnedString = String.valueOf(SessionRequestResult.SESSION_REQUEST_TRUE);
      doReturn(false).when(mainConnectionMock).claim();
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      sessionRequestService.start();
      long start = System.currentTimeMillis();
      long end = start + 2000;
      /*
       * The service should remain in a loop whilst the connection is not
       * free to use.
       */
      while (System.currentTimeMillis() < end) {
        SessionRequestResult result = sessionRequestService.getValue();
        assertNull(result);
        assertFalse(sessionRequestService.isFinished());
      }
      doReturn(true).when(mainConnectionMock).claim();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE, result);
        // Thread under test complete
        threadDone = true;
      });
    });

    while (!threadDone) {
      // This thread must wait until the thread under test is complete
      Thread.onSpinWait();
    }
  }

  /**
   * This is the successfulResultTest.
   */
  public void successfulResultTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(SessionRequestResult.FAILED_BY_NETWORK);
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      sessionRequestService.reset();
      sessionRequestService.start();

      sessionRequestService.setOnSucceeded(event -> {
        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }
        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByTutorNotLiveTest.
   */
  public void failedByTutorNotLiveTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE);
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      sessionRequestService.start();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByTutorNotOnlineTest.
   */
  public void failedByTutorNotOnlineTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE);
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      sessionRequestService.start();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE, result);
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
      doThrow(IOException.class).when(mainConnectionMock).sendString(any());
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      sessionRequestService.start();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}