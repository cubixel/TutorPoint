package application.controller.services;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * CLASS DESCRIPTION:
 *
 * @author CUBIXEL
 *
 */
public class XMLHandler {

    File file = null;
    Document doc = null;

    public XMLHandler() {
    }

    public void openFile(String path) {
        if (checkXML(path)) {
            file = new File(path);
            checkExists();
        }
    }

    public boolean hasFile() {
        if (file == null) {
            return false;
        } else {
            return file.exists();
        }
    }

    private boolean checkXML(String path) {
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

    public void parseToDOM() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
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

	public Boolean hasDOM() {
        String name = doc.getDocumentElement().getNodeName();
        System.out.println("Top Node named: '" + name + "'");

        if (name.equals("slideshow")){
            return true;
        } else {
            return false;
        }
		
    }

    public Document getDoc() {
        return doc;
    }
}