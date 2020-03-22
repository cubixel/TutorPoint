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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
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
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidText.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidLine() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidLine.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 5);
  }

  @Test
  public void ignoreInvalidLine() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidLine.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidShape() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidShape.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 8);
  }

  @Test
  public void ignoreInvalidShape() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidShape.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidShading() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidShading.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 5);
  }

  @Test
  public void ignoreInvalidShading() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidShading.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidAudio() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidAudio.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 6);
  }

  @Test
  public void ignoreInvalidAudio() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidAudio.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidImage() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidImage.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 4);
  }

  @Test
  public void ignoreInvalidImage() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidImage.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void addValidVideo() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLValidVideo.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 4);
  }

  @Test
  public void ignoreInvalidVideo() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLInvalidVideo.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 0);
  }

  @Test
  public void testMixedSlide() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/"
          + "TestXMLMixedSlide.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 7);
  }
}
