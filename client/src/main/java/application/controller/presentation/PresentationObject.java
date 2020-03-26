package application.controller.presentation;

import java.util.ArrayList;
import java.util.List;
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
    NodeList slides = toplevel.getElementsByTagName("slide");
    NodeList documentInfo = toplevel.getElementsByTagName("documentinfo");
    NodeList defaults = toplevel.getElementsByTagName("defaults");
    valid = true;
    //TODO - validate documentinfo and defaults, validate num of slides
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
          System.err.println("Slide rejected due to duplicate id");
        }
      }
    }

    if (slidesList.size() != totalSlides) {
      System.err.println("Presentation Rejected due to mismatch between totalslides attribute "
          + "and actual number of valid slides.");
      valid = false;
      return;
    }

    for (int i = 1; i <= slidesList.size(); i++) {
      if (slidesList.get(i).getId() != i) {
        System.err.println("Presentation Rejected due to unordered slides or discontinuity in "
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

  /**
   * METHOD DESCRIPTION.
   */
  public boolean setSlide(int id) { //TimingManager tmMgr) {
    if (id < 1 || id >= slidesList.size()) {
      return false;
    } else {
      /*PresentationSlide slide = slidesList.get(id);
      List<Node> elements = slide.getElementList();
      Node element;
      String elementName;
      NamedNodeMap attributes;
      tmMgr.clear();
      for (int i = 0; i < elements.LENGTH(); i++) {
        element = elements.item(i);
        elementName = element.getNodeName();
        attributes = element.getAttributes()
        switch (elementName) {
          case "text":
            textHandler.register(element, dfFont, dfFontSIze, dfFontColor);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("endtime"));
            break; 
          case "line":
            lineHandler.register(element, dfLineColor);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("endtime"));
            break; 
          case "shape":
            shapeHandler.register(element, dfFillColor);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("endtime"));
            break;
          case "audio":
            audioHandler.register(element);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("loop"));
            break; 
          case "image":
            shapeHandler.register(element);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("endtime"));
            break; 
          case "video":
            audioHandler.register(element);
            tmMgr.add(elementName, attributes.getNamedAttribute("starttime"), 
                attributes.getNamedAttribute("loop"));
            break; 
          default:
            break;
        }
      }

      tmMgr.addSlideTimer(slide.getDuration());
      //draw the slide with the right size and colours (colors, sorry) n shit
      //maybe do that before we tell the managers to do stuff
      //additionally: perhaps have a returned id from the elementManagers so that tmMgr can 
      //reference when drawing/undrawing
      */
      return true;
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