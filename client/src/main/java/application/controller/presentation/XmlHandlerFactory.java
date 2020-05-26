package application.controller.presentation;

/**
 * This class is necessary to test the PresentationWindowController class as
 * it means a Mockito version of the XmlHandler class can
 * be instantiated when the XmlHandlerFactory.createXmlHandler()
 * method is called.
 *
 * @author James Gardner
 */
public class XmlHandlerFactory {

  public XmlHandlerFactory() {

  }

  public XmlHandler createXmlHandler() {
    return new XmlHandler();
  }
}
