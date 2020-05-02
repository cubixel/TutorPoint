package application.model.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubjectRequestTest {

  private static int subjectId;

  private static SubjectRequest subjectRequest;

  /**
   * Creating a Subject instance to test on.
   */
  @BeforeEach
  public void setUp() {
    subjectId = 1;

    // TODO
    subjectRequest = new SubjectRequest(subjectId, null, 1);

  }

  @Test
  public void setFilePathTest() {
    assertEquals(subjectId, subjectRequest.getNumberOfSubjectsRequested());
    subjectRequest.setNumberOfSubjectsRequested(3);
    assertEquals(3, subjectRequest.getNumberOfSubjectsRequested());
  }

}
