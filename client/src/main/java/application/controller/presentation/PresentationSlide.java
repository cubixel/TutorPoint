package application.controller.presentation;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class PresentationSlide {
  private int id;
  private int duration;
  private boolean succeeded = false;
  private List<Node> elementList = new ArrayList<Node>(); 

  /**
   * CONSTRUCTOR DESCRIPTION.
   */
  public PresentationSlide(Node slide) {

    if (slide == null) {
      System.out.println("Handed NULL object");
      return;
    }

    if (slide.getAttributes().getLength() == 0) {
      System.out.println("Slide had no attributes");
      return;
    }

    try {
      id = Integer.parseInt(slide.getAttributes().getNamedItem("id").getNodeValue());
    } catch (NullPointerException nullE) {
      System.out.println("Slide had no ID attribute; ignored");
      return;
    } catch (NumberFormatException numberE) {
      System.out.println("Slide had malformed ID attribute; ignored. Provided id: "
          + slide.getAttributes().getNamedItem("id").getNodeValue());
      return;
    }

    try {
      duration = Integer.parseInt(slide.getAttributes().getNamedItem("duration").getNodeValue());
    } catch (NullPointerException nullE) {
      System.out.println("Slide had no Duration attribute; ignored");
      return;
    } catch (NumberFormatException numberE) {
      System.out.println("Slide had malformed Duration attribute; ignored. Provided duration: "
          + slide.getAttributes().getNamedItem("duration").getNodeValue());
      return;
    }
    System.out.println("Got Slide with ID: " + id + " and Duraton: " + duration);
    
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
  }

  private void addShape(Node childNode) {
  }

  private void addAudio(Node childNode) {
  }

  private void addImage(Node childNode) {
  }

  private void addVideo(Node childNode) {
  }

  public Object getId() {
    return id;
  }

  public List<Node> getElementList() {
    return elementList;
  }

  public boolean getSucceeded() {
    return succeeded;
  }
}