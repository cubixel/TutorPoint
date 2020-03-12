package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FileRequestTest {

  private static String filePath;
  private static FileRequest fileRequest;

  /**
   * Creating a Subject instance to test on.
   */
  @BeforeAll
  static void setUp() {
    filePath = "somepath";

    fileRequest = new FileRequest(filePath);

  }

  @Test
  public void setFilePathTest() {
    assertEquals(filePath, fileRequest.getFilePath());
    fileRequest.setFilePath("newPath");
    assertEquals("newPath", fileRequest.getFilePath());
  }

}