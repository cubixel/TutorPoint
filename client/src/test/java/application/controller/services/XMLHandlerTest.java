package application.controller.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class XMLHandlerTest {

    @Test
    public void openFile(){
        XMLHandler handler = new XMLHandler();
        handler.openFile("M:/Java/Github/TutorPoint/client/src/main/resources/application/media/TestXML.xml");

        assertTrue(handler.hasFile());
    }

    @Test
    public void verifyXML(){
        XMLHandler handler = new XMLHandler();
        handler.openFile("M:/Java/Github/TutorPoint/client/src/main/resources/application/media/TestImage.png");

        assertFalse(handler.hasFile());
    }

    @Test
    public void verifyExists(){
        XMLHandler handler = new XMLHandler();
        handler.openFile("M:/Java/Github/TutorPoint/client/src/main/resources/application/media/NonExistantXML.xml");

        assertFalse(handler.hasFile());
    }

    @Test
    public void parseToDOM(){
        XMLHandler handler = new XMLHandler();
        handler.openFile("M:/Java/Github/TutorPoint/client/src/main/resources/application/media/TestXML.xml");
        handler.parseToDOM();
        assertTrue(handler.hasDOM());
    }

}