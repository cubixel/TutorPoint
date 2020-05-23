package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import application.controller.presentation.exceptions.XmlLoadingException;
import org.junit.jupiter.api.Test;

public class XmlHandlerTest {

  @Test
  public void openFile() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/TestXML.xml");

      assertTrue(handler.hasFile());
    } catch (XmlLoadingException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void verifyXml() {
    
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/TestImage.png");
      fail();
    } catch (XmlLoadingException e) {
      assertTrue(e.getMessage().equals("File does not exist or is not .XML."));
    }
  }

  @Test
  public void verifyExists() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/NoXML.xml");
      fail();
    } catch (XmlLoadingException e) {
      assertTrue(e.getMessage().equals("File does not exist."));
    }
  }

  @Test
  public void parseToDom() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/TestXML.xml");
      assertTrue(handler.getDoc() != null 
          && handler.getDoc().getDocumentElement().getNodeName().equals("slideshow"));
    } catch (XmlLoadingException e) {
      e.printStackTrace();
      fail();
    }
  }

}
