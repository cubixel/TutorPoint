package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is testing the Message class. It tests the
 * constructor and confirms the fields within the
 * Message object take the values passed in and when
 * using getters and setters.
 *
 * @author Oliver Still
 */
public class MessageTest {
  private final String userName = "testUserName";
  private final int userID = 1;
  private final int sessionID = 2;
  private final String msg = "testMessage";

  private static Message message;

  @BeforeEach
  public void setup() {
    message = new Message(userName, userID, sessionID, msg);
  }

  @Test
  public void constructorTest() {
    assertEquals(userName, message.getUserName());
    assertEquals(userID, message.getUserID());
    assertEquals(sessionID, message.getSessionID());
    assertEquals(msg, message.getMsg());
  }
}
