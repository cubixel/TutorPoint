package application.controller.presentation;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class PresentationObject {

  private List<PresentationSlide> slidesList = new ArrayList<>();

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public PresentationObject(Document doc) {
    Element toplevel = doc.getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    for (int i = 0; i < slides.getLength(); i++) {
      PresentationSlide tempSlide = new PresentationSlide(slides.item(i));
      slidesList.add(i, tempSlide);
    }
  }

}