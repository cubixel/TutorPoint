package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SubjectTest {

  private static int id;
  private static String name;

  private static Subject subject;

  /**
   * Creating a Subject instance to test on.
   */
  @BeforeAll
  static void setUp() {
    id = 1;
    name = "someName";

    subject = new Subject(id, name);

  }

  @Test
  public void gettersTest() {
    assertEquals(id, subject.getId());
    assertEquals(name, subject.getName());
  }

}
