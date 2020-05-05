package application.controller.presentation;

import application.controller.presentation.exceptions.DefaultsException;
import application.controller.presentation.exceptions.DocumentInfoException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used to provide validation methods for validating w3c Documents
 * against the group schema, as well as validating data.
 *
 * @author CUBIXEL
 * @see    PresentationObject
 */
public class ElementValidations {
  
  private static final Logger log = LoggerFactory.getLogger("ElementValidations");

  /**
   * Validates text nodes to ensure that all necessary elements and attributes are present.
   * 
   * @param node w3c Node to be validated. 
   * @return If the text node is valid.
   */
  public static boolean validateText(Node node) {
    //check existence of font,fontsize,fontcolor, if there, check quality
    //check quality of xpos,ypos,starttime,endttime
    
    if (validateTextAttributes(node) && validateTextElements(node)) {
      log.info("Accepted text");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates line nodes to ensure that all necessary attributes are present.
   * 
   * @param node w3c Node to be validated.  
   * @return If the line node is valid.
   */
  public static boolean validateLine(Node node) {
    //check existence of linecolor, if there, check quality
    //check quality of xstart,ystart,xend,yend,starttime,endttime
    
    if (validateLineAttributes(node)) {
      log.info("Accepted line");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates shape nodes to ensure that all necessary elements and attributes are present.
   * 
   * @param node w3c Node to be validated. 
   * @return If the shape node is valid.
   */
  public static boolean validateShape(Node node) {
    //check existence of fillcolor, if there, check quality
    //check quality of type,xstart,ystart,width,height,starttime,endttime
    //also ensure there is only zero or one valid shading nodes, plus #text nodes
    
    if (validateShapeAttributes(node) && validateShapeElements(node)) {
      log.info("Accepted shape");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates audio nodes to ensure that all necessary attributes are present.
   * 
   * @param node w3c Node to be validated.  
   * @return If the audio node is valid.
   */
  public static boolean validateAudio(Node node) {
    //check quality of urlname,starttime,loop
    
    if (validateAudioAttributes(node)) {
      log.info("Accepted audio");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates image nodes to ensure that all necessary attributes are present.
   * 
   * @param node w3c Node to be validated.
   * @return If the image node is valid.
   */
  public static boolean validateImage(Node node) {
    //check quality of urlname,xstart,ystart,width,height,starttime,endtime
    if (validateImageAttributes(node)) {
      log.info("Accepted image");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates video nodes to ensure that all necessary attributes are present.
   * 
   * @param node w3c Node to be validated. 
   * @return If the video node is valid.
   */
  public static boolean validateVideo(Node node) {
    //check quality of urlname,starttime,loop,xstart,ystart
    if (validateVideoAttributes(node)) {
      log.info("Accepted video");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validates DocumentInfo nodes to ensure that all necessary elements are present.
   * 
   * @param documentInfo w3c Node to be containing DocumentInfo to be validated. 
   * @throws DocumentInfoException when a problem is found with DocumentInfo.
   */
  public static void validateDocumentInfo(NodeList documentInfo) throws DocumentInfoException {
    NodeList documentInfoChildren;
    Node childNode;
    String nodeName;
    List<Boolean> alreadyFoundFlags = new ArrayList<Boolean>();
    //flags to keep track of what elements have been found
    for (int i = 0; i < 5; i++) {
      alreadyFoundFlags.add(false);
    }
    if (documentInfo.getLength() == 1) {
      documentInfoChildren = documentInfo.item(0).getChildNodes();
      for (int i = 0; i < documentInfoChildren.getLength(); i++) {
        childNode = documentInfoChildren.item(i);
        nodeName = childNode.getNodeName();
        switch (nodeName) {
          case "author":
            //reject duplicate fields
            if (alreadyFoundFlags.get(0)) {
              throw new DocumentInfoException("Rejected due to duplicate 'author' field.", 
                  new Throwable());
            } else {
              alreadyFoundFlags.set(0, true);
              if (!validateStringElement(childNode)) {
                throw new DocumentInfoException("File rejected due to invalid 'author' field.", 
                    new Throwable());
              }
            }
            break;
          case "datemodified":
            //reject duplicate fields
            if (alreadyFoundFlags.get(1)) {
              throw new DocumentInfoException("File rejected due to duplicate 'datemodified' " 
                  + "field.", new Throwable());
            } else {
              alreadyFoundFlags.set(1, true);
              if (!validateStringElement(childNode)) {
                throw new DocumentInfoException("File rejected due to invalid 'datemodified' " 
                    + "field.", new Throwable());
              }
            }
            break;
          case "version":
            //reject duplicate fields
            if (alreadyFoundFlags.get(2)) {
              throw new DocumentInfoException("File rejected due to duplicate 'version' field.", 
                  new Throwable());
            } else {
              alreadyFoundFlags.set(2, true);
              if (!validateStringElement(childNode)) {
                throw new DocumentInfoException("File rejected due to invalid 'version' field.", 
                    new Throwable());
              }
            }
            break;
          case "totalslides":
            //reject duplicate fields
            if (alreadyFoundFlags.get(3)) {
              throw new DocumentInfoException("File rejected due to duplicate 'totalslides'" 
                  + " field.", new Throwable());
            } else {
              alreadyFoundFlags.set(3, true);

              if (!validateUnsignedIntElement(childNode)) {
                throw new DocumentInfoException("File rejected due to invalid 'totalslides' " 
                    + "field.", new Throwable());
              }
            }
            break;
          case "comment":
            //reject duplicate fields
            if (alreadyFoundFlags.get(4)) {
              throw new DocumentInfoException("File rejected due to duplicate 'comment' field.", 
                  new Throwable());
            } else {
              alreadyFoundFlags.set(4, true);
              if (!validateStringElement(childNode)) {
                throw new DocumentInfoException("File rejected due to invalid 'comment' field.", 
                    new Throwable());
              }
            }
            break;
          case "#text":
            //ignore #text elements
            break;  
          case "#comment":
            //ignore #comment elements (different to our comment elements)
            break; 
          default:
            throw new DocumentInfoException("File rejected due to unrecognised documentinfo " 
                + "element.", new Throwable());
        }
      }
      //check for missing elements
      if (alreadyFoundFlags.contains(false)) {
        throw new DocumentInfoException("File rejected due to missing documentinfo element(s).", 
                  new Throwable());
      }
    } else if (documentInfo.getLength() == 0) {
      throw new DocumentInfoException("File rejected due to nonexistant documentinfo.", 
                  new Throwable());
    } else {
      throw new DocumentInfoException("File rejected due to multiple documentinfo elements.", 
                  new Throwable());
    }
    return;
  }

  /**
   * Validates Defaults nodes to ensure that all necessary elements are present.
   * 
   * @param defaults w3c Node to be containing Defaults to be validated. 
   * @throws DefaultsException when a problem is found with Defaults.
   */
  public static void validateDefaults(NodeList defaults) throws DefaultsException {
    NodeList defaultsChildren;
    Node childNode;
    String nodeName;
    List<Boolean> alreadyFoundFlags = new ArrayList<Boolean>();
    //flags to keep track of what elements have been found
    for (int i = 0; i < 8; i++) {
      alreadyFoundFlags.add(false);
    }
    if (defaults.getLength() == 1) {
      defaultsChildren = defaults.item(0).getChildNodes();
      for (int i = 0; i < defaultsChildren.getLength(); i++) {
        childNode = defaultsChildren.item(i);
        nodeName = childNode.getNodeName();
        switch (nodeName) {
          case "backgroundcolor":
            //reject duplicate fields
            if (alreadyFoundFlags.get(0)) {
              throw new DefaultsException("File rejected due to duplicate 'backgroundcolor' " 
                  + "field.", new Throwable());
            } else {
              alreadyFoundFlags.set(0, true);
              if (!validateColorElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'backgroundcolor' " 
                    + "field.",  new Throwable());
              }
            }
            break;
          case "font":
            //reject duplicate fields
            if (alreadyFoundFlags.get(1)) {
              throw new DefaultsException("File rejected due to duplicate 'font' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(1, true);
              if (!validateStringElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'font' field.", 
                    new Throwable());
              } else {
                List<String> availableFonts = Font.getFontNames();
                if (availableFonts.contains(childNode.getFirstChild().getNodeValue())) {
                  // Valid Font
                } else {
                  throw new DefaultsException("File rejected due to invalid font '" 
                      + childNode.getFirstChild().getNodeValue() + "'.", 
                      new Throwable());
                }
              }
            }
            break;
          case "fontsize":
            //reject duplicate fields
            if (alreadyFoundFlags.get(2)) {
              throw new DefaultsException("File rejected due to duplicate 'fontsize' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(2, true);
              if (!validateUnsignedIntElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'fontsize' field.", 
                    new Throwable());
              }
            }
            break;
          case "fontcolor":
            //reject duplicate fields
            if (alreadyFoundFlags.get(3)) {
              throw new DefaultsException("File rejected due to duplicate 'fontcolor' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(3, true);
              if (!validateColorElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'fontcolor' field.", 
                    new Throwable());
              }
            }
            break;
          case "linecolor":
            //reject duplicate fields
            if (alreadyFoundFlags.get(4)) {
              throw new DefaultsException("File rejected due to duplicate 'linecolor' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(4, true);
              if (!validateColorElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'linecolor' field.", 
                    new Throwable());
              }
            }
            break;
          case "fillcolor":
            //reject duplicate fields
            if (alreadyFoundFlags.get(5)) {
              throw new DefaultsException("File rejected due to duplicate 'fillcolor' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(5, true);
              if (!validateColorElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'fillcolor' field.", 
                    new Throwable());
              }
            }
            break;
          case "slidewidth":
            //reject duplicate fields
            if (alreadyFoundFlags.get(6)) {
              throw new DefaultsException("File rejected due to duplicate 'slidewidth' field.", 
                    new Throwable());
            } else {
              alreadyFoundFlags.set(6, true);
              if (!validateUnsignedIntElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'slidewidth' field.", 
                    new Throwable());
              }
            }
            break;
          case "slideheight":
            //reject duplicate fields
            if (alreadyFoundFlags.get(7)) {
              throw new DefaultsException("File rejected due to duplicate 'slideheight' field.", 
                  new Throwable());
            } else {
              alreadyFoundFlags.set(7, true);
              if (!validateUnsignedIntElement(childNode)) {
                throw new DefaultsException("File rejected due to invalid 'slideheight' field.", 
                    new Throwable());
              }
            }
            break;
          case "#text":
            //ignore #text elements
            break;  
          case "#comment":
            //ignore #comment elements (different to our comment elements)
            break; 
          default:
            throw new DefaultsException("File rejected due to unrecognised defaults element.", 
                new Throwable());
        }
      }
      //check for missing elements
      if (alreadyFoundFlags.contains(false)) {
        throw new DefaultsException("File rejected due to missing defaults element(s).", 
            new Throwable());
      }
    } else if (defaults.getLength() == 0) {
      throw new DefaultsException("File rejected due to nonexistant defaults.", 
          new Throwable());
    } else {
      throw new DefaultsException("File rejected due to multiple defaults elements.",
          new Throwable());
    }
    return;
  }

  private static boolean validateTextElements(Node node) {
    NodeList children = node.getChildNodes();
    
    // Reject empty text element
    if (children.getLength() == 0) {
      log.error("Rejected due to empty text element");
      return false;
    }

    for (int i = 0; i < children.getLength(); i++) {
      //Check if this child node is just text
      if (children.item(i).getNodeName().equals("#text")) {
        //Valid, as #text only exists if text is present (due to "normalise()" call when parsing)

      //Check if this child node is a bold element
      } else if (children.item(i).getNodeName().equals("b")) {
        //The bold element can only contain a #text element
        if (children.item(i).getChildNodes().getLength() == 1) {
          //Chck the one child element is in fact a #text element
          if (children.item(i).getChildNodes().item(0).getNodeName().equals("#text")) {
            //The bold element only contained 1 child node, which was #text
          } else {
            //The bold element contained an element other than #text as its one element
            log.error("Rejected due to incorrect element in bold tag");
            return false;
          }
        } else {
          // The bold element contained multiple children (or had 0 children and must be empty)
          log.error("Rejected due to incorrect child number for bold tag");
          return false;
        }
      //Check if this child node is a italic element
      } else if (children.item(i).getNodeName().equals("i")) {
        //The italic element can only contain a #text element
        if (children.item(i).getChildNodes().getLength() == 1) {
          //Chck the one child element is in fact a #text element
          if (children.item(i).getChildNodes().item(0).getNodeName().equals("#text")) {
            //The italic element only contained 1 child node, which was #text
          } else {
            //The italic element contained an element other than #text as its one element
            log.error("Rejected due to incorrect element in italic tag");
            return false;
          }
        } else {
          // The italic element contained multiple children (or had 0 children and must be empty)
          log.error("Rejected due to incorrect child number for italic tag");
          return false;
        }
      } else {
        log.error("Rejected due to invalid element in top level");
        return false;
      }
    }

    return true;
  }

  private static boolean validateShapeElements(Node node) {
    NodeList children = node.getChildNodes();
    Node child;
    if (children.getLength() == 1) {
      child = children.item(0);
      if (child.getNodeName() == "shading") {
        if (validateShadingAttributes(child)) {
          return true;
        } else {
          log.error("Rejected due to invalid shading element.");
          return false;
        }
      } else {
        log.error("Rejected due to unacceptable element.");
        return false;
      }
    } else if (children.getLength() > 1) {
      log.error("Rejected due to too many shape sub-elements.");
      return false;
    } else {
      return true;
    }
  }

  private static boolean validateTextAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node attribute = null;

    // Font does not have to exist, but if it does then it must be valid
    attribute = attributes.getNamedItem("font");
    if (attribute != null) {
      List<String> availableFonts = Font.getFontNames();
      if (availableFonts.contains(attribute.getNodeValue())) {
        // Valid Font
      } else {
        log.error("Rejected due to invalid font");
        return false;
      }
    }

    // Font size does not have to exist, but if it does then it must be valid
    if (!validateIntegerAttribute(attributes, "fontsize", false)) {
      return false;
    }

    // Font color does not have to exist, but if it does then it must be valid
    if (!validateColorAttribute(attributes, "fontcolor", false)) {
      return false;
    }

    // xpos has to exist and be valid
    if (!validateFloatAttribute(attributes, "xpos", true)) {
      return false;
    }
 
    // ypos has to exist and be valid
    if (!validateFloatAttribute(attributes, "ypos", true)) {
      return false;
    }

    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // endtime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "endtime", true)) {
      return false;
    }

    //ensure valid values of start and end time
    try {
      if (!validateStartEndTimes(
          Integer.parseInt(attributes.getNamedItem("starttime").getNodeValue()), 
          Integer.parseInt(attributes.getNamedItem("endtime").getNodeValue()))) {
        return false;
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    return true;
  }

  private static boolean validateLineAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();

    // Line color does not have to exist, but if it does then it must be valid
    if (!validateColorAttribute(attributes, "linecolor", false)) {
      return false;
    }

    // xstart has to exist and be valid
    if (!validateFloatAttribute(attributes, "xstart", true)) {
      return false;
    }
 
    // ystart has to exist and be valid
    if (!validateFloatAttribute(attributes, "ystart", true)) {
      return false;
    }

    // xend has to exist and be valid
    if (!validateFloatAttribute(attributes, "xend", true)) {
      return false;
    }
 
    // yend has to exist and be valid
    if (!validateFloatAttribute(attributes, "yend", true)) {
      return false;
    }

    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // endtime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "endtime", true)) {
      return false;
    }

    //ensure valid values of start and end time
    try {
      if (!validateStartEndTimes(
          Integer.parseInt(attributes.getNamedItem("starttime").getNodeValue()), 
          Integer.parseInt(attributes.getNamedItem("endtime").getNodeValue()))) {
        return false;
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    return true;
  }

  private static boolean validateShapeAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node attribute = null;

    // type has to exist, and must be 'oval' or 'rectangle'
    attribute = attributes.getNamedItem("type");
    if (attribute != null) {
      if (attribute.getNodeValue().equals("oval") || attribute.getNodeValue().equals("rectangle")) {
        //valid shape type
      } else {
        log.error("Rejected due to invalid 'type' attribute.");
        return false;
      }
    } else {
      log.error("Rejected due to missing 'type' attribute.");
      return false;
    }

    // Shape color does not have to exist, but if it does then it must be valid
    if (!validateColorAttribute(attributes, "fillcolor", false)) {
      return false;
    }

    // xstart has to exist and be valid
    if (!validateFloatAttribute(attributes, "xstart", true)) {
      return false;
    }
 
    // ystart has to exist and be valid
    if (!validateFloatAttribute(attributes, "ystart", true)) {
      return false;
    }

    // width has to exist and be valid
    if (!validateFloatAttribute(attributes, "width", true)) {
      return false;
    }
 
    // height has to exist and be valid
    if (!validateFloatAttribute(attributes, "height", true)) {
      return false;
    }

    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // endtime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "endtime", true)) {
      return false;
    }

    //ensure valid values of start and end time

    if (!validateStartEndTimes(
        Integer.parseInt(attributes.getNamedItem("starttime").getNodeValue()), 
        Integer.parseInt(attributes.getNamedItem("endtime").getNodeValue()))) {
      return false;
    }

    return true;
  }

  private static boolean validateShadingAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    
    // x1 has to exist and be valid
    if (!validateIntegerAttribute(attributes, "x1", true)) {
      return false;
    }

    // y1 has to exist and be valid
    if (!validateIntegerAttribute(attributes, "y1", true)) {
      return false;
    }

    // color1 must exist and be valid
    if (!validateColorAttribute(attributes, "color1", true)) {
      return false;
    }

    // x2 has to exist and be valid
    if (!validateIntegerAttribute(attributes, "x2", true)) {
      return false;
    }

    // y2 has to exist and be valid
    if (!validateIntegerAttribute(attributes, "y2", true)) {
      return false;
    }

    // color2 must exist and be valid
    if (!validateColorAttribute(attributes, "color2", true)) {
      return false;
    }

    // 'cyclic has to exist and be valid
    if (!validateBooleanAttribute(attributes, "cyclic", true)) {
      return false;
    }

    return true;
  }

  private static boolean validateAudioAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    //define potential extensions
    ArrayList<String> extensions = new ArrayList<String>();
    extensions.add(".wav");
    extensions.add(".mp3");

    // urlname has to exist and be valid
    if (!validateUrlAttribute(attributes, "urlname", true, extensions)) {
      return false;
    }

    if (!AudioHandler.validateUrl(attributes.getNamedItem("urlname").getTextContent())) {
      log.error("Rejected due to unloadable url.");
      return false;
    }
 
    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // starttime must be +ve
    if (Integer.parseInt(attributes.getNamedItem("starttime").getNodeValue()) < 0) {
      return false;
    }

    // loop has to exist and be valid
    if (!validateBooleanAttribute(attributes, "loop", true)) {
      return false;
    }
 
    return true;
  }

  private static boolean validateImageAttributes(Node node) {
    //define potential extensions
    ArrayList<String> extensions = new ArrayList<String>();
    extensions.add(".gif");
    extensions.add(".jpg");
    extensions.add(".jpeg");
    extensions.add(".png");

    NamedNodeMap attributes = node.getAttributes();
    // urlname has to exist and be valid
    if (!validateUrlAttribute(attributes, "urlname", true, extensions)) {
      return false;
    }

    if (!ImageHandler.validateUrl(attributes.getNamedItem("urlname").getTextContent())) {
      log.error("Rejected due to unloadable url.");
      return false;
    }

    // xstart has to exist and be valid
    if (!validateFloatAttribute(attributes, "xstart", true)) {
      return false;
    }
 
    // ystart has to exist and be valid
    if (!validateFloatAttribute(attributes, "ystart", true)) {
      return false;
    }

    // width has to exist and be valid
    if (!validateFloatAttribute(attributes, "width", true)) {
      return false;
    }
 
    // height has to exist and be valid
    if (!validateFloatAttribute(attributes, "height", true)) {
      return false;
    }

    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // endtime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "endtime", true)) {
      return false;
    }

    //ensure valid values of start and end time
    if (!validateStartEndTimes(
        Integer.parseInt(attributes.getNamedItem("starttime").getNodeValue()), 
        Integer.parseInt(attributes.getNamedItem("endtime").getNodeValue()))) {
      return false;
    }

    return true;
  }

  private static boolean validateVideoAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    //define potential extensions
    ArrayList<String> extensions = new ArrayList<String>();
    extensions.add(".vlc"); 
    extensions.add(".m3u8");//TODO more extensions?

    // urlname has to exist and be valid
    if (!validateUrlAttribute(attributes, "urlname", true, extensions)) {
      return false;
    }

    if (!VideoHandler.validateUrl(attributes.getNamedItem("urlname").getTextContent())) {
      log.error("Rejected due to unloadable url.");
      return false;
    }

    // starttime has to exist and be valid
    if (!validateIntegerAttribute(attributes, "starttime", true)) {
      return false;
    }

    // loop has to exist and be valid
    if (!validateBooleanAttribute(attributes, "loop", true)) {
      return false;
    }

    // xstart has to exist and be valid
    if (!validateFloatAttribute(attributes, "xstart", true)) {
      return false;
    }
 
    // ystart has to exist and be valid
    if (!validateFloatAttribute(attributes, "ystart", true)) {
      return false;
    }

    return true;
  }

  private static boolean validateColorString(String colorString) {
    if (colorString.startsWith("#") && (colorString.length() == 7)) {
      // Correct start and length
    } else {
      return false;
    }
    //Check the string is valid hex by attempting the conversion to Integer
    try {
      Integer.parseUnsignedInt(colorString.substring(1), 16);
    } catch (NumberFormatException e) {
      // Not a valid hex string
      return false;
    }

    return true;
  }

  private static boolean validateFloat(String floatString) {
    //Check the string is a valid decimal by attempting the conversion to Float
    try {
      Float.parseFloat(floatString);
    } catch (NumberFormatException e) {
      // Not a valid float
      return false;
    }
    return true;
  }

  private static boolean validateInteger(String intString) {
    //Check the string is a valid integer by attempting the conversion to Integer
    try {
      Integer.parseInt(intString);
    } catch (NumberFormatException e) {
      // Not a valid integer
      return false;
    }
    return true;
  }

  private static boolean validateBoolean(String boolString) {
    //Check the string is a valid Boolean by comparing to 'true', 'false', '1' and '0'
    if (boolString.equals("true") || boolString.equals("false") 
        || boolString.equals("1") || boolString.equals("0")) {
      return true;
    } else {
      return false;
    }
  }

  private static boolean validateUrl(String urlString, List<String> extensions) {
    //Check the string is a valid URL by checking ending extension against supplied list.
    //Does not ensure that url points at accessible file.
    if (urlString.startsWith("http")) {
      for (String extension: extensions) {
        if (urlString.length() > extension.length()) {
          if (urlString.substring(urlString.length() - extension.length()).equals(extension)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static boolean validateIntegerAttribute(NamedNodeMap attributes, String attributeName,
      Boolean mustExist) {
    Node attribute = null;
    attribute = attributes.getNamedItem(attributeName);
    if (attribute != null) {
      if (validateInteger(attribute.getNodeValue())) {
        //Valid Integer
        return true;
      } else {
        log.error("Rejected due to invalid '" + attributeName + "' attribute.");
        return false;
      }
    } else if (mustExist == true) {
      log.error("Rejected due to missing '" + attributeName + "' attribute.");
      return false;
    }
    return true;
  }

  private static boolean validateColorAttribute(NamedNodeMap attributes, String attributeName,
      Boolean mustExist) {
    Node attribute = null;
    attribute = attributes.getNamedItem(attributeName);
    if (attribute != null) {
      if (validateColorString(attribute.getNodeValue())) {
        //Valid Color String
        return true;
      } else {
        log.error("Rejected due to invalid '" + attributeName + "' attribute.");
        return false;
      }
    } else if (mustExist == true) {
      log.error("Rejected due to missing '" + attributeName + "' attribute.");
      return false;
    }
    return true;
  }

  private static boolean validateFloatAttribute(NamedNodeMap attributes, String attributeName,
      Boolean mustExist) {
    Node attribute = null;
    attribute = attributes.getNamedItem(attributeName);
    if (attribute != null) {
      if (validateFloat(attribute.getNodeValue())) {
        float floatVal = Float.parseFloat(attribute.getNodeValue());
        if (0 <= floatVal && floatVal <= 100) {
          //Valid Float
          return true;
        } else {
          log.error("Rejected due to '" + attributeName + "' not in range 0-100.");
          return false;
        }
        
      } else {
        log.error("Rejected due to invalid '" + attributeName + "' attribute.");
        return false;
      }
    } else if (mustExist == true) {
      log.error("Rejected due to missing '" + attributeName + "' attribute.");
      return false;
    }
    return true;
  }

  private static boolean validateBooleanAttribute(NamedNodeMap attributes, String attributeName,
      Boolean mustExist) {
    Node attribute = null;
    attribute = attributes.getNamedItem(attributeName);
    if (attribute != null) {
      if (validateBoolean(attribute.getNodeValue())) {
        //Valid Boolean
        return true;
      } else {
        log.error("Rejected due to invalid '" + attributeName + "' attribute.");
        return false;
      }
    } else if (mustExist == true) {
      log.error("Rejected due to missing '" + attributeName + "' attribute.");
      return false;
    }
    return true;
  }

  private static boolean validateUrlAttribute(NamedNodeMap attributes, String attributeName,
      Boolean mustExist, List<String> extensions) {
    Node attribute = null;
    attribute = attributes.getNamedItem(attributeName);
    if (attribute != null) {
      if (validateUrl(attribute.getNodeValue(), extensions)) {
        //Valid Url
        return true;
      } else {
        log.error("Rejected due to invalid '" + attributeName + "' attribute.");
        return false;
      }
    } else if (mustExist == true) {
      log.error("Rejected due to missing '" + attributeName + "' attribute.");
      return false;
    }
    return true;
  }

  private static boolean validateStartEndTimes(int startTime, int endTime) {
    if (startTime >= 0) {
      if (endTime > 0) {
        if (startTime < endTime) {
          //valid start and end times
        } else {
          log.error("Rejected due to start time after end time.");
          return false;
        }
      } else if (endTime == -1) {
        //valid, as starttime is +ve and endtime is -1
      } else {
        log.error("Rejected due to end time not being positive or -1.");
        return false;
      }
    } else {
      log.error("Rejected due to negative start time.");
      return false;
    }
    return true;
  }

  private static boolean validateStringElement(Node element) {
    if (element.getChildNodes().getLength() == 1) {
      //valid number of nodes
      return true;
    } else if (element.getChildNodes().getLength() == 0) {
      //reject empty field
      log.error("Rejected due to empty '" + element.getNodeName() + "' field.");
      return false;
    } else {
      log.error("Rejected due to overfull '" + element.getNodeName() + "' field.");
      return false;
    }
  }

  private static boolean validateUnsignedIntElement(Node element) {
    if (element.getChildNodes().getLength() == 1) {
      //valid number of nodes
      try {
        Integer.parseUnsignedInt(element.getFirstChild().getNodeValue());
      } catch (NumberFormatException e) {
        log.error("Rejected due to invalid '" + element.getNodeName() + "' field.");
        return false;
      }
      return true;
    } else if (element.getChildNodes().getLength() == 0) {
      //reject empty field
      log.error("Rejected due to empty '" + element.getNodeName() + "' field.");
      return false;
    } else {
      log.error("Rejected due to overfull '" + element.getNodeName() + "' field.");
      return false;
    }
  }

  private static boolean validateColorElement(Node element) {
    if (element.getChildNodes().getLength() == 1) {
      //valid number of nodes
      if (validateColorString(element.getFirstChild().getNodeValue())) {
        //valid color
      } else {
        log.error("Rejected due to invalid '" + element.getNodeName() + "' field.");
        return false;
      }
      return true;
    } else if (element.getChildNodes().getLength() == 0) {
      //reject empty field
      log.error("Rejected due to empty '" + element.getNodeName() + "' field.");
      return false;
    } else {
      log.error("Rejected due to overfull '" + element.getNodeName() + "' field.");
      return false;
    }
  }
}