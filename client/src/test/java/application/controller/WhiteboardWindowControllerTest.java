package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * CLASS DESCRIPTION:
 * This class tests the WhiteboardWindowController. It tests
 * the functionality of the whiteboard's functionality.
 *
 * @author CUBIXEL
 *
 */
public class WhiteboardWindowControllerTest {

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    protected MainConnection mainConnectionMock;

    @Mock
    protected ViewFactory viewFactoryMock;

    protected Canvas canvas;

    protected Slider widthSlider;

    protected ColorPicker colorPicker;

    protected WhiteboardWindowController whiteboardWindowController;

    public void selectTool() {
        // User selects pen.
        // whiteboardWindowController.selectTool("pen");
        // Check pen is selected and active.
        // assertTrue(whiteboardWindowController.selectedTool, "pen");
    }

    public void testChangeColor() {
        // User selects new pen color.
        whiteboardWindowController.setPenColor(Color.DARKSALMON);
        // Check pen selected pen color is active.
        assertEquals(whiteboardWindowController.getPenColor(), Color.DARKSALMON);
        System.out.println("Tested Colour Picker");
    }

    public void testChangeWidth() {
        whiteboardWindowController.setPenWidth(10);
        assertEquals(whiteboardWindowController.getPenWidth(), 10);
        System.out.println("Tested Pen Width Slider");
    }

    public void testDrawLine() {


    }
}
