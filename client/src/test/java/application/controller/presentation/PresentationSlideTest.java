package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.services.XmlHandler;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class PresentationSlideTest {
  @Test
  public void makeSlide() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXML.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getSucceeded());
  }

  @Test
  public void testXmlNoId() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLNoID.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getSucceeded());
  }

  @Test
  public void testXmlBadId() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLBadID.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getSucceeded());
  }

  @Test
  public void testXmlNoDuration() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLNoDuration.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getSucceeded());
  }

  @Test
  public void testXmlBadDuration() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLBadDuration.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getSucceeded());
  }

  @Test
  public void testXmlNoAttributes() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLNoAttributes.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertFalse(testSlide.getSucceeded());
  }

  @Test
  public void addValidText() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLValidText.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 9);
  }

  @Test
  public void ignoreInvalidText() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "M:/Java/Github/TutorPoint/client/src/main/resources/application/media/XML/"
          + "TestXMLInvalidText.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }
}
