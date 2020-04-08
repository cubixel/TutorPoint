package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.FileDownloadResult;
import application.model.requests.FileRequest;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class FileDownloadServiceTest {

  private FileDownloadService fileDownloadService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private FileRequest fileRequestMock;


  
  /**
   * METHOD DESCRIPTION.
   */
  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }


  /**
   * METHOD DESCRIPTION.
   * 
   * @throws Exception DESCRIPTION
   */
  @BeforeEach
  public void setUp() throws Exception {
    initMocks(this);

    when(mainConnectionMock.listenForString()).thenReturn(returnedString);

    fileDownloadService = new FileDownloadService(mainConnectionMock, fileRequestMock);
  }

  /**
   * METHOD DESCRIPTION.
   *
   * @throws IOException DESCRIPTION
   */
  @AfterEach
  public void cleanUp() throws IOException {

  }

  @Test
  public void successfulResultTest() {
    returnedString = String.valueOf(FileDownloadResult.SUCCESS);

    Platform.runLater(() -> {
      fileDownloadService.start();
      fileDownloadService.setOnSucceeded(event -> {
        FileDownloadResult result = fileDownloadService.getValue();

        assertEquals(FileDownloadResult.SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    returnedString = String.valueOf(FileDownloadResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      fileDownloadService.start();
      fileDownloadService.setOnSucceeded(event -> {
        FileDownloadResult result = fileDownloadService.getValue();

        assertEquals(FileDownloadResult.FAILED_BY_NETWORK, result);
      });
    });
  }

}
