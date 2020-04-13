package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubjectTest {
  private int id;
  private String name;
  private String category;

  private static Subject subject;

  /**
   * Creating a Subject instance to test on.
   */
  @BeforeEach
  void setUp() {
    this.id = 1;
    this.name = "someName";
    this.category = "someCategory";

    subject = new Subject(id, name, category);

  }

  @Test
  public void gettersTest() {
    assertEquals(id, subject.getId());
    assertEquals(name, subject.getName());
    assertEquals(category, subject.getCategory());
  }

}
