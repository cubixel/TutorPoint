package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.managers.SubjectManager;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public class SubjectRendererTest {

  private SubjectRenderer subjectRenderer;

  private String returnedString;

  @Mock
  private MainConnection mainConnectionMock;

  @Mock
  private SubjectManager subjectManagerMock;

  @Mock
  private HBox horizontalBoxMock;

  @BeforeAll
  public static void setUpToolkit() {
    /* This method starts the JavaFX runtime. The specified Runnable will then be
     * called on the JavaFX Application Thread. */
    Platform.startup(() -> System.out.println("Toolkit initialized ..."));
  }

  @BeforeEach
  public void setUp() throws Exception {
    initMocks(this);
    when(mainConnectionMock.listenForString()).thenReturn(returnedString);
  }

}
