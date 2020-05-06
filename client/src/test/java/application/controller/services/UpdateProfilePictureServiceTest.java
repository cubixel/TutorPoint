package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.FileUploadResult;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class UpdateProfilePictureServiceTest {

  private UpdateProfilePictureService updateProfilePictureService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private File fileMock;

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

    updateProfilePictureService = new UpdateProfilePictureService(fileMock, mainConnectionMock);
  }

  @Test
  public void successfulResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(FileUploadResult.FILE_UPLOAD_SUCCESS);

    Platform.runLater(() -> {
      updateProfilePictureService.start();
      updateProfilePictureService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(new String());
        } catch (IOException e) {
          fail();
        }

        FileUploadResult result = updateProfilePictureService.getValue();

        assertEquals(FileUploadResult.FILE_UPLOAD_SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(FileUploadResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      updateProfilePictureService.start();
      updateProfilePictureService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        FileUploadResult result = updateProfilePictureService.getValue();

        assertEquals(FileUploadResult.FAILED_BY_NETWORK, result);
      });
    });
  }

  @Test
  public void unexpectedErrorResultTest() {
    // Setting Mock return value.
    returnedString = String.valueOf(FileUploadResult.FAILED_BY_SERVER_ERROR);

    Platform.runLater(() -> {
      updateProfilePictureService.start();
      updateProfilePictureService.setOnSucceeded(event -> {

        try {
          verify(mainConnectionMock, times(1)).sendString(any());
        } catch (IOException e) {
          fail();
        }

        FileUploadResult result = updateProfilePictureService.getValue();

        assertEquals(FileUploadResult.FAILED_BY_SERVER_ERROR, result);
      });
    });
  }

}
