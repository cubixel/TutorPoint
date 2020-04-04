package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SubjectTest {

  private static int id;
  private static String name;
  private static String nameOfThumbnailFile;
  private static String thumbnailPath;
  private static String coverPhotoFilename;
  private static String coverPhotoPath;

  private static Subject subject;

  /**
   * Creating a Subject instance to test on.
   */
  @BeforeAll
  static void setUp() {
    id = 1;
    name = "someName";
    nameOfThumbnailFile = "file.png";
    thumbnailPath = "somepath";
    coverPhotoFilename = "someFileName";
    coverPhotoPath = "somepath";

    subject = new Subject(id, name, nameOfThumbnailFile, thumbnailPath, coverPhotoFilename, coverPhotoPath);

  }

  @Test
  public void gettersTest() {
    assertEquals(id, subject.getId());
    assertEquals(name, subject.getName());
    assertEquals(nameOfThumbnailFile, subject.getNameOfThumbnailFile());
    assertEquals(thumbnailPath, subject.getThumbnailPath());
  }

}
