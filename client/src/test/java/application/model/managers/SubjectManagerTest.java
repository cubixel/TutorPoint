package application.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import application.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Test class for the SubjectManager.
 *
 * @author James Gardner
 * @see Subject
 * @see SubjectManager
 */
public class SubjectManagerTest {
  private SubjectManager subjectManager;

  @Mock
  Subject subjectMock;

  @BeforeEach
  public void setUp() {
    initMocks(this);
    subjectManager = new SubjectManager();
  }

  @Test
  public void addAndPopTutorTest() {
    assertEquals(0, subjectManager.getNumberOfSubjects());
    subjectManager.addSubject(subjectMock);
    assertEquals(1, subjectManager.getNumberOfSubjects());
    subjectManager.popSubject();
    assertEquals(0, subjectManager.getNumberOfSubjects());
  }

  @Test
  public void getLastTutorTest() {
    subjectManager.addSubject(subjectMock);
    assertEquals(subjectMock, subjectManager.getLastSubject());
  }

  @Test
  public void getElementNumberTest() {
    Subject subjectOne = new Subject(1, "subjectOne", "Test", false);
    Subject subjectTwo = new Subject(2, "subjectTwo", "Test", false);
    Subject subjectThree = new Subject(3, "subjectThree", "Test", false);
    Subject subjectFour = new Subject(4, "subjectFour", "Test", false);
    subjectManager.addSubject(subjectOne);
    subjectManager.addSubject(subjectTwo);
    subjectManager.addSubject(subjectThree);
    subjectManager.addSubject(subjectFour);
    assertEquals(0, subjectManager.getElementNumber("subjectOne"));
    assertEquals(1, subjectManager.getElementNumber("subjectTwo"));
    assertEquals(2, subjectManager.getElementNumber("subjectThree"));
    assertEquals(3, subjectManager.getElementNumber("subjectFour"));
    assertEquals(-1, subjectManager.getElementNumber("NotInSubjectManager"));
  }
}
