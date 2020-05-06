package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.FileUploadResult;
import application.controller.enums.SessionRequestResult;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SessionRequestServiceTest {

  private SessionRequestService sessionRequestService;
  private String returnedString;
  private int userID;
  private int sessionID;
  private Boolean leavingSession;
  private Boolean isHost;

  @Mock
  private MainConnection mainConnectionMock;

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

    userID = 1;
    sessionID = 1;
    leavingSession = false;
    isHost = true;

    sessionRequestService = new SessionRequestService(mainConnectionMock, userID, sessionID,
        leavingSession, isHost);
  }

  @Test
  public void test() {
    long start = System.currentTimeMillis();
    long end = start + 5000;
    System.out.println(end);
    while (System.currentTimeMillis() < end) {
      System.out.println(System.currentTimeMillis() - start);
    }
  }

  @Test
  public void claimingConnectionTest() {
    doReturn(false).when(mainConnectionMock).claim();

    // Setting Mock return value.
    returnedString = String.valueOf(SessionRequestResult.SESSION_REQUEST_TRUE);

    Platform.runLater(() -> {
      sessionRequestService.start();
      long start = System.currentTimeMillis();
      long end = start + 5000;
      System.out.println(end);
      while (System.currentTimeMillis() < end) {
        SessionRequestResult result = sessionRequestService.getValue();
        assertNull(result);
      }
      doReturn(true).when(mainConnectionMock).claim();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.SESSION_REQUEST_TRUE, result);
      });
    });
  }

  @Test
  public void successfulResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(SessionRequestResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      sessionRequestService.start();
      sessionRequestService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
          verify(mainConnectionMock, times(1)).release();
        } catch (IOException e) {
          fail();
        }

        SessionRequestResult result = sessionRequestService.getValue();

        assertEquals(SessionRequestResult.FAILED_BY_NETWORK, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE);

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
      });
    });
  }

  @Test
  public void unexpectedErrorResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE);

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
      });
    });
  }

}