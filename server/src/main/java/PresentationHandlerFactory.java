/**
 * This class is necessary to test the Session class as
 * it means a Mockito version of the PresentationHandler class can
 * be instantiated when the PresentationHandlerFactory.createPresentationHandler()
 * method is called.
 *
 * @author James Gardner
 */
public class PresentationHandlerFactory {

  public PresentationHandlerFactory() {
  }

  public PresentationHandler createPresentationHandler(Session session) {
    return new PresentationHandler(session);
  }
}