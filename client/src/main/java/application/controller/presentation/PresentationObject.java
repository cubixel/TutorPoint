package application.controller.presentation;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class PresentationObject {

  private static final Logger log = LoggerFactory.getLogger("PresentationObject Logger");

  private List<PresentationSlide> slidesList = new ArrayList<>();
  private Boolean valid = false;

  private String author;
  private String dateModified;
  private String version;
  private int totalSlides;
  private String comment;

  private String dfBackgroundColor;
  private String dfFont;
  private int dfFontSize;
  private String dfFontColor;
  private String dfLineColor;
  private String dfFillColor;
  private int dfSlideWidth;
  private int dfSlideHeight;

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public PresentationObject(Document doc) {
    Element toplevel = doc.getDocumentElement();
    PresentationSlide tempSlide;
    boolean idAvailable = false;
    NodeList documentInfo = toplevel.getElementsByTagName("documentinfo");
    NodeList defaults = toplevel.getElementsByTagName("defaults");
    valid = true;
    if (ElementValidations.validateDocumentInfo(documentInfo)) {
      extractDocumentInfo(documentInfo);
    } else {
      valid = false;
      return;
    }
    if (ElementValidations.validateDefaults(defaults)) {
      extractDefaults(defaults);
    } else {
      valid = false;
      return;
    }

    NodeList slides = toplevel.getElementsByTagName("slide");
    for (int i = 0; i < slides.getLength(); i++) {
      tempSlide = new PresentationSlide(slides.item(i));
      if (tempSlide.getSucceeded()) {
        idAvailable = true;
        for (int j = 0; j < slidesList.size(); j++) {
          if (tempSlide.getId() == slidesList.get(j).getId()) {
            idAvailable = false;
          }
        }
        if (idAvailable) {  
          slidesList.add(tempSlide);
        } else {
          log.error("Slide rejected due to duplicate id");
        }
      }
    }

    if (slidesList.size() != totalSlides) {
      log.error("Presentation Rejected due to mismatch between totalslides attribute "
          + "and actual number of valid slides.");
      valid = false;
      return;
    }

    if (slidesList.size() == 0) {
      log.error("Presentation Rejected as zero slides were successfully registered");
      valid = false;
      return;
    }

    for (int i = 0; i < slidesList.size(); i++) {
      if (slidesList.get(i).getId() != i) {
        log.error("Presentation Rejected due to unordered slides or discontinuity in "
            + "slide IDs");
        valid = false;
        return;
      }
    }

    
  }

  private void extractDocumentInfo(NodeList documentInfo) {
    NodeList documentInfoChildren = documentInfo.item(0).getChildNodes();
    Node childNode;
    String nodeName;
    for (int i = 0; i < documentInfoChildren.getLength(); i++) {
      childNode = documentInfoChildren.item(i);
      nodeName = childNode.getNodeName();
      switch (nodeName) {
        case "author":
          author = childNode.getFirstChild().getNodeValue();
          break;
        case "datemodified":
          dateModified = childNode.getFirstChild().getNodeValue();
          break;
        case "version":
          version = childNode.getFirstChild().getNodeValue();
          break;
        case "totalslides":
          totalSlides = Integer.parseUnsignedInt(childNode.getFirstChild().getNodeValue());
          break;
        case "comment":
          comment = childNode.getFirstChild().getNodeValue();
          break; 
        default:
          break;
      } 
    }  
  }

  private void extractDefaults(NodeList defaults) {
    NodeList defaultsChildren = defaults.item(0).getChildNodes();
    Node childNode;
    String nodeName;
    for (int i = 0; i < defaultsChildren.getLength(); i++) {
      childNode = defaultsChildren.item(i);
      nodeName = childNode.getNodeName();
      switch (nodeName) {
        case "backgroundcolor":
          dfBackgroundColor = childNode.getFirstChild().getNodeValue();
          break; 
        case "font":
          dfFont = childNode.getFirstChild().getNodeValue();
          break; 
        case "fontsize":
          dfFontSize = Integer.parseUnsignedInt(childNode.getFirstChild().getNodeValue());
          break;
        case "fontcolor":
          dfFontColor = childNode.getFirstChild().getNodeValue();
          break; 
        case "linecolor":
          dfLineColor = childNode.getFirstChild().getNodeValue();
          break; 
        case "fillcolor":
          dfFillColor = childNode.getFirstChild().getNodeValue();
          break; 
        case "slidewidth":
          dfSlideWidth = Integer.parseUnsignedInt(childNode.getFirstChild().getNodeValue());
          break;
        case "slideheight":
          dfSlideHeight = Integer.parseUnsignedInt(childNode.getFirstChild().getNodeValue());
          break;
        default:
          break;
      }
    }
  }

  public Boolean getValid() {
    return valid;
  }

  public String getAuthor() {
    return author;
  }
  
  public String getDateModified() {
    return dateModified;
  }

  public String getVersion() {
    return version;
  }

  public int getTotalSlides() {
    return totalSlides;
  }

  public String getComment() {
    return comment;
  }

  public String getDfBackgroundColor() {
    return dfBackgroundColor;
  }

  public String getDfFont() {
    return dfFont;
  }

  public int getDfFontSize() {
    return dfFontSize;
  }

  public String getDfFontColor() {
    return dfFontColor;
  }

  public String getDfLineCOlor() {
    return dfLineColor;
  }

  public String getDfFillColor() {
    return dfFillColor;
  }

  public int getDfSlideWidth() {
    return dfSlideWidth;
  }

  public int getDfSlideHeight() {
    return dfSlideHeight;
  }

  public List<PresentationSlide> getSlidesList() {
    return slidesList;
  }

}