package application.controller.presentation;

import application.controller.presentation.exceptions.PresentationCreationException;
import org.w3c.dom.Document;

public class PresentationObjectFactory {

  public PresentationObjectFactory() {

  }

  public PresentationObject createPresentationObject(Document doc)
      throws PresentationCreationException {
    return new PresentationObject(doc);
  }
}
