package application.model;

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
  static void setUp() {
    subjectId = 1;

    subjectRequest = new SubjectRequest(subjectId);

  }

  @Test
  public void setFilePathTest() {
    assertEquals(subjectId, subjectRequest.getId());
    subjectRequest.setId(3);
    assertEquals(3, subjectRequest.getId());
  }

}
