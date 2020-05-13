package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TutorTest {

  private Tutor tutor;
  private final int userID = 1;
  private final String someUsername = "someUsername";
  private final int rating = 5;
  private final boolean isFollowed = true;

  @BeforeEach
  public void setup() {
    tutor = new Tutor(someUsername, userID, rating, isFollowed);
  }

  @Test
  public void constructorTest() {
    assertEquals(userID, tutor.getUserID());
    assertEquals(someUsername, tutor.getUsername());
    assertEquals(rating, tutor.getRating());
    assertEquals(isFollowed, tutor.isFollowed());
    assertFalse(tutor.isLive());
  }

  @Test
  public void getterAndSetterTest() {
    assertEquals(isFollowed, tutor.isFollowed());
    tutor.setFollowed(!isFollowed);
    assertEquals(!isFollowed, tutor.isFollowed());

    assertFalse(tutor.isLive());
    tutor.setLive(true);
    assertTrue(tutor.isLive);
  }
}
