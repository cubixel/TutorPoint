package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.controller.enums.SubjectRequestResult;
import application.model.SubjectRequest;
import application.model.managers.SubjectManager;
import java.io.IOException;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SubjectRequestServiceTest {

  private SubjectRequestService subjectRequestService;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private SubjectRequest subjectRequestMock;

  @Mock
  private SubjectManager subjectManagerMock;

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

    subjectRequestService = new SubjectRequestService(mainConnectionMock, subjectManagerMock);
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
    //TODO Changes need to be made to tests.
    returnedString = String.valueOf(SubjectRequestResult.SUCCESS);

    Platform.runLater(() -> {
      subjectRequestService.start();
      subjectRequestService.setOnSucceeded(event -> {
        SubjectRequestResult result = subjectRequestService.getValue();

        assertEquals(SubjectRequestResult.SUCCESS, result);
      });
    });
  }

  @Test
  public void networkFailResultTest() {
    returnedString = String.valueOf(SubjectRequestResult.FAILED_BY_NETWORK);

    Platform.runLater(() -> {
      subjectRequestService.start();
      subjectRequestService.setOnSucceeded(event -> {
        SubjectRequestResult result = subjectRequestService.getValue();

        assertEquals(SubjectRequestResult.FAILED_BY_NETWORK, result);
      });
    });
  }
}
