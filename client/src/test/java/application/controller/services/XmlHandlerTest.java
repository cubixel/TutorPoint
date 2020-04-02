package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class XmlHandlerTest {

  @Test
  public void openFile() {
    XmlHandler handler = new XmlHandler();
    handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TestXML.xml");

    assertTrue(handler.hasFile());
  }

  @Test
  public void verifyXml() {
    XmlHandler handler = new XmlHandler();
    handler.makeXmlFromUrl(
          "src/main/resources/application/media/TestImage.png");

    assertFalse(handler.hasFile());
  }

  @Test
  public void verifyExists() {
    XmlHandler handler = new XmlHandler();
    handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/NoXML.xml");
    assertFalse(handler.hasFile());
  }

  @Test
  public void parseToDom() {
    XmlHandler handler = new XmlHandler();
    handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TestXML.xml");
    assertTrue(handler.getDoc() != null 
        && handler.getDoc().getDocumentElement().getNodeName().equals("slideshow"));
  }

}
