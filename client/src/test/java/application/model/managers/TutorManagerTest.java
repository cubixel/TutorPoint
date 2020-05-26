package application.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.Tutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Test class for the TutorManager.
 *
 * @author James Gardner
 * @see Tutor
 * @see TutorManager
 */
public class TutorManagerTest {
  private TutorManager tutorManager;

  @Mock
  Tutor tutorMock;

  @BeforeEach
  public void setUp() {
    initMocks(this);
    tutorManager = new TutorManager();
  }

  @Test
  public void addAndPopTutorTest() {
    assertEquals(0, tutorManager.getNumberOfTutors());
    tutorManager.addTutor(tutorMock);
    assertEquals(1, tutorManager.getNumberOfTutors());
    tutorManager.popTutor();
    assertEquals(0, tutorManager.getNumberOfTutors());
  }

  @Test
  public void getLastTutorTest() {
    tutorManager.addTutor(tutorMock);
    assertEquals(tutorMock, tutorManager.getLastTutor());
  }
}