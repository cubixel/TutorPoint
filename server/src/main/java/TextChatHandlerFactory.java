/**
 * This class is necessary to test the Session class as
 * it means a Mockito version of the TextChatHandler class can
 * be instantiated when the TextChatHandlerFactory.createTextChatHandler()
 * method is called.
 *
 * @author James Gardner
 */
public class TextChatHandlerFactory {

  public TextChatHandlerFactory() {
  }

  public TextChatHandler createTextChatHandler(Session session) {
    return new TextChatHandler(session);
  }
}