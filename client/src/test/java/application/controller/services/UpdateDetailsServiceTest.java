package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.AccountUpdateResult;
import application.model.Account;
import application.model.updates.AccountUpdate;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class UpdateDetailsServiceTest {

  private UpdateDetailsService updateDetailsService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private AccountUpdate accountUpdateMock;

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

    updateDetailsService = new UpdateDetailsService(accountUpdateMock, mainConnectionMock);
  }

  @Test
  public void successfulResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS);

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(AccountUpdateResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_NETWORK, result);
      });
    });
  }

  @Test
  public void unexpectedErrorResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(AccountUpdateResult.FAILED_BY_UNEXPECTED_ERROR);

    Platform.runLater(() -> {
      updateDetailsService.start();
      updateDetailsService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
        } catch (IOException e) {
          fail();
        }

        AccountUpdateResult result = updateDetailsService.getValue();

        assertEquals(AccountUpdateResult.FAILED_BY_UNEXPECTED_ERROR, result);
      });
    });
  }

}
