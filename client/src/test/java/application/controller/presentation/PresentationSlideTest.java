package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;

import application.controller.services.XmlHandler;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PresentationSlideTest {
  @Test
  public void makeSlide() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/TestXML.xml");
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getId() == null);
  }
}