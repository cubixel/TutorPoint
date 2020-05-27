package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.presentation.exceptions.XmlLoadingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests PresentationSlide. Not to actually be run, use
 * SlideTest
 *
 * @author Daniel Bishop
 * @author Eric Walker
 */
public class PresentationSlideTest {

  public void makeSlide() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXML.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testXmlNoId() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLNoID.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertFalse(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testXmlBadId() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLBadID.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertFalse(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testXmlNoDuration() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLNoDuration.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertFalse(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testXmlBadDuration() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLBadDuration.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertFalse(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testXmlNoAttributes() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLNoAttributes.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertFalse(testSlide.getSucceeded());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidText() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidText.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 9);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidText() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidText.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidLine() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidLine.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 5);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidLine() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidLine.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidShape() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidShape.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 8);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidShape() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidShape.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidShading() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidShading.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 5);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidShading() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidShading.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidAudio() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidAudio.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 6);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidAudio() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidAudio.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      System.out.println(testSlide.getElementList().size());
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidImage() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidImage.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 4);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidImage() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidImage.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void addValidVideo() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLValidVideo.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 4);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void ignoreInvalidVideo() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLInvalidVideo.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }

  public void testMixedSlide() {
    try {
      XmlHandler handler = new XmlHandler();
      Document xmlDoc = handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/PresentationSlide/"
            + "TestXMLMixedSlide.xml");
      Element toplevel = xmlDoc.getDocumentElement();
      NodeList slides = toplevel.getElementsByTagName("slide");
      PresentationSlide testSlide = new PresentationSlide(slides.item(0));
      assertTrue(testSlide.getElementList().size() == 7);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
  }
}