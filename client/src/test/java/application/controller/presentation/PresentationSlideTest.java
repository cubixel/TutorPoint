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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
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
          "src/main/resources/application/media/XML/PresentationSlide/"
          + "TestXMLMixedSlide.xml");
    handler.parseToDom();
    Element toplevel = (handler.getDoc()).getDocumentElement();
    NodeList slides = toplevel.getElementsByTagName("slide");
    PresentationSlide testSlide = new PresentationSlide(slides.item(0));
    assertTrue(testSlide.getElementList().size() == 7);
  }
}
