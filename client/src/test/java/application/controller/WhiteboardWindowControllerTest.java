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

    public void testSelectTool() {
        // User selects new tool.
        whiteboardWindowController.setTool("pen");

        // Check new tool is selected and active.
        assertEquals(whiteboardWindowController.getSelectedTool(), "pen");

        System.out.println("Tool Select - Test Complete");
    }

    public void testChangeColor() {
        // User selects new stroke color.
        whiteboardWindowController.setPenColor(Color.DARKBLUE);

        // Check selected stroke color is active.
        assertEquals(whiteboardWindowController.getPenColor(), Color.DARKBLUE);

        System.out.println("Stroke Color Select - Test Complete");
    }

    public void testChangeWidth() {
        // User selects new stroke width.
        whiteboardWindowController.setPenWidth(10);

        // Check selected stroke width is active.
        assertEquals(whiteboardWindowController.getPenWidth(), 10);

        System.out.println("Stroke Width Select - Test Complete");
    }

    public void testDrawLine() {
        //whiteboardWindowController.createLine()
        //whiteboardWindowController.draw()

    }
}
