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

import application.controller.enums.FollowTutorResult;
import java.io.IOException;
import javafx.application.Platform;
import org.mockito.Mock;

/**
 * Test class for the FollowTutorRequestService. Tests all return values
 * that can be received from the Server and also checks the error
 * handling from Network errors.
 *
 * @author James Gardner
 * @see FollowTutorRequestService
 */
public class FollowTutorRequestServiceTest {

  protected FollowTutorRequestService followTutorRequestService;
  protected String returnedString;
  protected volatile boolean threadDone;

  @Mock
  protected MainConnection mainConnectionMock;

  /**
   * This is the claimingConnectionTest.
   */
  public void claimingConnectionTest() {
    try {
      returnedString = String.valueOf(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS);
      doReturn(false).when(mainConnectionMock).claim();
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      followTutorRequestService.start();
      long start = System.currentTimeMillis();
      long end = start + 2000;
      /*
       * The service should remain in a loop whilst the connection is not
       * free to use.
       */
      while (System.currentTimeMillis() < end) {
        FollowTutorResult result = followTutorRequestService.getValue();
        assertNull(result);
        assertFalse(followTutorRequestService.isFinished());
      }
      doReturn(true).when(mainConnectionMock).claim();
      followTutorRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }

        FollowTutorResult result = followTutorRequestService.getValue();

        assertEquals(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS, result);
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
      returnedString = String.valueOf(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.claim()).thenReturn(true);
    } catch (IOException e) {
      fail(e);
    }
    Platform.runLater(() -> {
      followTutorRequestService.start();
      followTutorRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }

        FollowTutorResult result = followTutorRequestService.getValue();

        assertEquals(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }

  /**
   * This is the failedByDatabaseErrorTest.
   */
  public void failedByDatabaseErrorTest() {
    // Setting Mock return value.
    try {
      returnedString = String.valueOf(FollowTutorResult.FAILED_BY_DATABASE_ERROR);
      when(mainConnectionMock.listenForString()).thenReturn(returnedString);
      when(mainConnectionMock.claim()).thenReturn(true);
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      followTutorRequestService.start();
      followTutorRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }

        FollowTutorResult result = followTutorRequestService.getValue();

        assertEquals(FollowTutorResult.FAILED_BY_DATABASE_ERROR, result);
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
      when(mainConnectionMock.claim()).thenReturn(true);
      doThrow(IOException.class).when(mainConnectionMock).sendString(any());
    } catch (IOException e) {
      fail(e);
    }

    Platform.runLater(() -> {
      followTutorRequestService.start();
      followTutorRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        FollowTutorResult result = followTutorRequestService.getValue();

        assertEquals(FollowTutorResult.FAILED_BY_NETWORK, result);
        threadDone = true;
      });
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }
  }
}