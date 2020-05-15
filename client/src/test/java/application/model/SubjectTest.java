package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is testing the Subject class. It tests the
 * constructor and confirms the fields within the
 * Subject object take the values passed in and when
 * using getters and setters.
 *
 * @author James Gardner
 */
public class SubjectTest {
  private final int id = 1;
  private final String name = "testName";
  private final String category = "testCategory";
  private final boolean isFollowed = true;

  private static Subject subject;

  @BeforeEach
  public void setup() {
    subject = new Subject(id, name, category, isFollowed);
  }

  @Test
  public void constructorTest() {
    assertEquals(id, subject.getId());
    assertEquals(name, subject.getName());
    assertEquals(category, subject.getCategory());
    assertEquals(isFollowed, subject.isFollowed());
  }

  @Test
  public void getterAndSetterTest() {
    assertEquals(isFollowed, subject.isFollowed());
    subject.setFollowed(!isFollowed);
    assertEquals(!isFollowed, subject.isFollowed());

    int newID = 2;
    assertEquals(id, subject.getId());
    subject.setId(newID);
    assertEquals(newID, subject.getId());

    String newName = "newName";
    assertEquals(name, subject.getName());
    subject.setName(newName);
    assertEquals(newName, subject.getName());

    String newCategory = "newCategory";
    assertEquals(category, subject.getCategory());
    subject.setCategory(newCategory);
    assertEquals(newCategory, subject.getCategory());
  }
}
