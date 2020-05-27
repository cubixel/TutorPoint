package application.controller.presentation;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class to contain data regarding a particular presentation slide.
 * Extracts data from a provided slide node and validates it.
 *
 * @author Daniel Bishop
 * @author Eric Walker
 * @see ElementValidations
 * @see PresentationObject
 */
public class PresentationSlide {
  private static final Logger log = LoggerFactory.getLogger("PresentationSlide");
  private int id;
  private int duration;
  private boolean succeeded = false;
  private List<Node> elementList = new ArrayList<Node>(); 

  /**
   * Creates a presentation slide by extracting data from a provided w3c node containing slide data.
   * Will reject invalid slide elements. getSucceeded() can be used to check if 
   * created slide is valid or not.
   * @param slide A w3c Node containing slide data.
   */
  public PresentationSlide(Node slide) {

    //check slide existance and metadata
    if (slide == null) {
      log.error("Handed NULL object");
      return;
    }

    if (slide.getAttributes().getLength() == 0) {
      log.error("Slide had no attributes");
      return;
    }

    try {
      id = Integer.parseUnsignedInt(slide.getAttributes().getNamedItem("id").getNodeValue());
    } catch (NullPointerException nullE) {
      log.error("Slide had no ID attribute; ignored");
      return;
    } catch (NumberFormatException numberE) {
      log.error("Slide had malformed ID attribute; ignored. Provided id: "
          + slide.getAttributes().getNamedItem("id").getNodeValue());
      return;
    }

    try {
      duration = Integer.parseInt(slide.getAttributes().getNamedItem("duration").getNodeValue());
    } catch (NullPointerException nullE) {
      log.error("Slide had no Duration attribute; ignored");
      return;
    } catch (NumberFormatException numberE) {
      log.error("Slide had malformed Duration attribute; ignored. Provided duration: "
          + slide.getAttributes().getNamedItem("duration").getNodeValue());
      return;
    }
    if (duration < 0 && duration != -1) {
      log.error("Slide had invalid Duration attribute; ignored");
      return;
    }
    //log.info("Got Slide with ID: " + id + " and Duraton: " + duration);
    
    //check slide sub-elements and add to elementList
    NodeList children = slide.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childNode = children.item(i);
      String nodeName = childNode.getNodeName();
      switch (nodeName) {
        case "text":
          addText(childNode);
          break;
        case "line":
          addLine(childNode);
          break;
        case "shape":
          addShape(childNode);
          break;
        case "audio":
          addAudio(childNode);
          break;
        case "image":
          addImage(childNode);
          break;
        case "video":
          addVideo(childNode);
          break;
            
        default:
          break;
      }
    }

    succeeded = true;
    
  }

  private void addText(Node childNode) {
    if (ElementValidations.validateText(childNode)) {
      elementList.add(childNode);
    }
  }

  private void addLine(Node childNode) {
    if (ElementValidations.validateLine(childNode)) {
      elementList.add(childNode);
    }
  }

  private void addShape(Node childNode) {
    if (ElementValidations.validateShape(childNode)) {
      elementList.add(childNode);
    }
  }

  private void addAudio(Node childNode) {
    if (ElementValidations.validateAudio(childNode)) {
      elementList.add(childNode);
    }
  }

  private void addImage(Node childNode) {
    if (ElementValidations.validateImage(childNode)) {
      elementList.add(childNode);
    }
  }

  private void addVideo(Node childNode) {
    if (ElementValidations.validateVideo(childNode)) {
      elementList.add(childNode);
    }
  }

  public int getId() {
    return id;
  }

  public int getDuration() {
    return duration;
  }

  public List<Node> getElementList() {
    return elementList;
  }

  public boolean getSucceeded() {
    return succeeded;
  }
}