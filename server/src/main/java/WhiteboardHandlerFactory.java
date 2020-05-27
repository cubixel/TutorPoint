/**
 * This class is necessary to test the Session class as
 * it means a Mockito version of the WhiteboardHandler class can
 * be instantiated when the WhiteboardHandlerFactory.createWhiteboardHandler()
 * method is called.
 *
 * @author James Gardner
 */
public class WhiteboardHandlerFactory {

  public WhiteboardHandlerFactory() {
  }

  public WhiteboardHandler createWhiteboardHandler(Session session, boolean tutorOnlyAccess) {
    return new WhiteboardHandler(session, tutorOnlyAccess);
  }
}