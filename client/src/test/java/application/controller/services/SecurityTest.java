package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;



public class SecurityTest {

  @Test
  public void hashTest() {
    String testString = "thisisatest";
    String hashedString = "51bdbbadebdd4d81ea94f84f99f326f52091a918fc39d570e4e1d8d34a46ec42";
    assertEquals(hashedString, Security.hashPassword(testString));
  }
}