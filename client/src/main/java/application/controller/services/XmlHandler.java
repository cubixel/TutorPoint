package application.controller.services;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class XmlHandler {

  File file = null;
  Document doc = null;

  public XmlHandler() {
  }

  /**
   * METHOD DESCRIPTION.
   */
  public Document makeXmlFromUrl(String path) {
    openFile(path);
    if (file != null) {
      if (checkExists()) {
        parseToDom();
        if (hasDom()) {
          return doc;
        }
      }
    }
    return null;
  
  }

  private void openFile(String path) {
    if (checkXml(path)) {
      file = new File(path);
    }
  }

  /**
   * METHOD DESCRIPTION.
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
      System.out.println("Input File '" + path + "' is not of type .xml");
      return false;
    }
  }

  private boolean checkExists() {
    if (file.exists()) {
      return true;
    } else {
      // TODO Make proper error message
      System.out.println("Input file '" + file.getAbsolutePath() + "' does not exist");
      file = null;
      return false;
    }
  }

  private void parseToDom() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      doc = docBuilder.parse(file);
      doc.getDocumentElement().normalize();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Boolean hasDom() {
    String name = doc.getDocumentElement().getNodeName();
    System.out.println("Top Node named: '" + name + "'");

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
