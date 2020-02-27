package application.controller.presentation;

import java.util.List;
import javafx.scene.text.Font;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementValidations {
    
  /**
   * METHOD DESCRIPTION.
   */
  public static boolean validateText(Node node) {
    //check existance of font,fontsize,fontcolor, if there, check quality
    //check quality of xpos,ypos,starttime,endttime
    
    if (validateTextAttributes(node) && validateTextElements(node)) {
      System.err.println("Accepted");
      return true;
    } else {
      return false;
    }
  }

  private static boolean validateTextElements(Node node) {
    NodeList children = node.getChildNodes();
    
    // Reject empty text element
    if (children.getLength() == 0) {
      System.err.println("Rejected due to empty text element");
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
            System.err.println("Rejected due to incorrect element in bold tag");
            return false;
          }
        } else {
          // The bold element contained multiple children (or had 0 children and must be empty)
          System.err.println("Rejected due to incorrect child number for bold tag");
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
            System.err.println("Rejected due to incorrect element in italic tag");
            return false;
          }
        } else {
          // The italic element contained multiple children (or had 0 children and must be empty)
          System.err.println("Rejected due to incorrect child number for italic tag");
          return false;
        }
      } else {
        System.err.println("Rejected due to invalid element in top level");
        return false;
      }
    }

    return true;
  }

  private static boolean validateTextAttributes(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node attribute = null;

    // Font does not have to exist, but if it des then it must be valid
    attribute = attributes.getNamedItem("font");
    if (attribute != null) {
      List<String> availableFonts = Font.getFontNames();
      if (availableFonts.contains(attribute.getNodeValue())) {
        // Valid Font
      } else {
        System.err.println("Rejected due to invalid font");
        return false;
      }
    }

    // Font size does not have to exist, but if it des then it must be valid
    attribute = attributes.getNamedItem("fontsize");
    if (attribute != null) {
      if (validateInteger(attribute.getNodeValue())) {
        //Valid Integer
      } else {
        System.err.println("Rejected due to invalid fontsize");
        return false;
      }
    }

    // Font color does not have to exist, but if it does then it must be valid
    attribute = attributes.getNamedItem("fontcolor");
    if (attribute != null) {
      if (validateColorString(attribute.getNodeValue())) {
        // Valid colour string
      } else {
        System.err.println("Rejected due to invalid fontcolour");
        return false;
      }
    }

    // xpos has to exist and be valid
    attribute = attributes.getNamedItem("xpos");
    if (attribute != null) {
      if (validateFloat(attribute.getNodeValue())) {
        // Valid float
      } else {
        System.err.println("Rejected due to invalid xpos");
        return false;
      }
    } else {
      System.err.println("Rejected due to missing xpos");
      return false;
    }

    // ypos has to exist and be valid
    attribute = attributes.getNamedItem("ypos");
    if (attribute != null) {
      if (validateFloat(attribute.getNodeValue())) {
        // Valid float
      } else {
        System.err.println("Rejected due to invalid ypos");
        return false;
      }
    } else {
      System.err.println("Rejected due to missing ypos");
      return false;
    }

    // starttime has to exist and be valid
    attribute = attributes.getNamedItem("starttime");
    if (attribute != null) {
      if (validateInteger(attribute.getNodeValue())) {
        // Valid int
      } else {
        System.err.println("Rejected due to invalid starttime");
        return false;
      }
    } else {
      System.err.println("Rejected due to missing starttime");
      return false;
    }

    // endtime has to exist and be valid
    attribute = attributes.getNamedItem("endtime");
    if (attribute != null) {
      if (validateInteger(attribute.getNodeValue())) {
        // Valid int
      } else {
        System.err.println("Rejected due to invalid endtime");
        return false;
      }
    } else {
      System.err.println("Rejected due to missing endtime");
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

}