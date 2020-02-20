package application.controller.presentation;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * CLASS DESCRIPTION:
 *
 * @author CUBIXEL
 *
 */
public class PresentationSlide {
    private int id;
    public PresentationSlide(Node slide){
        NodeList children = slide.getChildNodes();
        for (int i = 0; i < children.getLength(); i++){
            Node childNode = children.item(i);
            String nodeName = childNode.getNodeName();
            switch (nodeName) {
                case "id":
                    id = Integer.parseInt(childNode.getNodeValue());
                    break;
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
    }
	private void addVideo(Node childNode) {
	}
	private void addImage(Node childNode) {
	}
	private void addAudio(Node childNode) {
	}
	private void addShape(Node childNode) {
	}
	private void addLine(Node childNode) {
	}
	private void addText(Node childNode) {
    }
	public Object getId() {
		return null;
	}

}