package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

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

    public void testWhiteboardInitialisation() {
        assertNotNull(whiteboardWindowController.getWhiteboard());

        System.out.println("Whiteboard Initialisation - Test Complete");
    }

    public void testSelectTool() {
        // User selects new tool.
        whiteboardWindowController.setTool("pen");

        // Check new tool is selected and active.
        assertEquals(whiteboardWindowController.getSelectedTool(), "pen");

        System.out.println("Tool Select - Test Complete");
    }

    public void testChangeColor() {
        // User selects new stroke color.
        whiteboardWindowController.setPenColor(Color.BLACK);

        // Check selected stroke color is active.
        assertEquals(whiteboardWindowController.getPenColor(), Color.BLACK);

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

        canvas = whiteboardWindowController.getWhiteboard();

        // Details of mock mouse event input here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/MouseEvent.html

        MouseEvent mousePressedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_PRESSED, 200, 200, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, false, false, false, null);

        canvas.fireEvent(mousePressedEvent);

        assertEquals(whiteboardWindowController.getMouseState(), "pressed");

        MouseEvent mouseDraggedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_DRAGGED, 200, 200, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, false, false, false, null);

        canvas.fireEvent(mouseDraggedEvent);

        assertEquals(whiteboardWindowController.getMouseState(), "dragged");

        MouseEvent mouseReleasedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_RELEASED, 200, 200, 0, 0, MouseButton.PRIMARY, 0,
                false, false, false, false, false, false, false, false, false, false, null);

        canvas.fireEvent(mouseReleasedEvent);

        assertEquals(whiteboardWindowController.getMouseState(), "released");

    }

    // TODO - Test Draw Shapes

    // TODO - Test
}
