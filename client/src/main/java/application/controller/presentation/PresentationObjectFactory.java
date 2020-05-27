package application.controller.presentation;

import application.controller.presentation.exceptions.PresentationCreationException;
import org.w3c.dom.Document;

/**
 * This class is necessary to test the PresentationWindowController class as
 * it means a Mockito version of the PresentationObject class can
 * be instantiated when the PresentationObjectFactory.createPresentationObject()
 * method is called.
 *
 * @author James Gardner
 */
public class PresentationObjectFactory {

  public PresentationObjectFactory() {

  }

  public PresentationObject createPresentationObject(Document doc)
      throws PresentationCreationException {
    return new PresentationObject(doc);
  }
}
