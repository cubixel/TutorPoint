package application.controller.presentation;

import application.controller.presentation.exceptions.DomParsingException;
import application.controller.presentation.exceptions.XmlLoadingException;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is used to load an XML file into a w3c Document.
 * It contains the makeXmlFromUrl method to do so.
 *
 * @author Daniel Bishop
 * @author Eric Walker
 */
public class XmlHandler {

  private static final Logger log = LoggerFactory.getLogger("XmlHandler");

  File file = null;
  Document doc = null;

  public XmlHandler() {
  }

  /**
   * Creates and returns a w3c Document from the file at the supplied URL.
   * 
   * @param path The URL to use as the source file.
   * @return the w3c Document created from the file.
   * @throws XmlLoadingException If failed to load XML.
   */
  public Document makeXmlFromUrl(String path) throws XmlLoadingException {
    openFile(path);
    if (file != null) {
      if (checkExists()) {
        try {
          parseToDom();
          if (hasDom()) {
            return doc;
          } else {
            throw new XmlLoadingException("File is not a slideshow document.", new Throwable());
          }
        } catch (DomParsingException e) {
          throw new XmlLoadingException("File could not be parsed to DOM.", e);
        }
      } else {
        throw new XmlLoadingException("File does not exist.", new Throwable());
      }
    } else {
      throw new XmlLoadingException("File does not exist or is not .XML.", new Throwable());
    }
  }

  private void openFile(String path) {
    if (checkXml(path)) {
      file = new File(path);
    }
  }

  /**
   * Checks whether the file URL points to a valid file.
   * @return true if URL point to valid file.
   */
  public boolean hasFile() {
    if (file == null) {
      return false;
    } else {
      return file.exists();
    }
  }

  private boolean checkXml(String path) {
    if (path.endsWith(".xml")) {
      return true;
    } else {
      // TODO Make proper error message
      log.error("Input File '" + path + "' is not of type .xml");
      return false;
    }
  }

  private boolean checkExists() {
    if (file.exists()) {
      return true;
    } else {
      // TODO Make proper error message
      log.error("Input file '" + file.getAbsolutePath() + "' does not exist");
      file = null;
      return false;
    }
  }

  private void parseToDom() throws DomParsingException {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      doc = docBuilder.parse(file);
      doc.getDocumentElement().normalize();
    } catch (ParserConfigurationException e) {
      throw new DomParsingException("Failed to parse to DOM", e);
    } catch (SAXException e) {
      throw new DomParsingException("Failed to parse to DOM", e);
    } catch (IOException e) {
      throw new DomParsingException("Failed to parse to DOM", e);
    }
  }

  private Boolean hasDom() {
    String name = doc.getDocumentElement().getNodeName();
    log.info("Top Node named: '" + name + "'");

    if (name.equals("slideshow")) {
      return true;
    } else {
      return false;
    }
  
  }

  public Document getDoc() {
    return doc;
  }
}
