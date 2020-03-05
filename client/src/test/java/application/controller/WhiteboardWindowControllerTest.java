package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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

  /**
   * Tests that the whiteboard canvas has been initialised and is not null.
   */
  public void testWhiteboardInitialisation() {
    assertNotNull(whiteboardWindowController.getWhiteboard());

    System.out.println("Whiteboard Initialisation - Test Complete");
  }

  /**
   * Test the tool select getters and setters in WhiteboardWindowController.
   * Sets the tool type, before asserting the string is equal to the getter's value.
   */
  public void testSelectTool() {
    // User selects new tool.
    whiteboardWindowController.setTool("pen");

    // Check new tool is selected and active.
    assertEquals(whiteboardWindowController.getSelectedTool(), "pen");

    System.out.println("Tool Select - Test Complete");
  }

  /**
   * Test the penColor getters and setters in WhiteboardWindowController.
   * Sets the pen color, before asserting the value is equal to the getter's hex value.
   */
  public void testChangeColor() {
    // User selects new stroke color.
    whiteboardWindowController.setPenColor(Color.BLACK);

    // Check selected stroke color is active.
    assertEquals(whiteboardWindowController.getPenColor(), Color.BLACK);

    System.out.println("Stroke Color Select - Test Complete");
  }

  /**
   * Test the penWidth getters and setters in WhiteboardWindowController.
   * Sets the pen width, before asserting the value is equal to the getter's value.
   */
  public void testChangeWidth() {
    // User selects new stroke width.
    whiteboardWindowController.setPenWidth(10);

    // Check selected stroke width is active.
    assertEquals(whiteboardWindowController.getPenWidth(), 10);

    System.out.println("Stroke Width Select - Test Complete");
  }

  /**
   * Test the drawLine method in WhiteboardWindowController.
   * Asserts the mouseState variable after each mouse event is fired to the canvas.
   * If all asserts pass, a new line is perceived to be drawn to the canvas.
   */
  public void testDrawLine() {

    canvas = whiteboardWindowController.getWhiteboard();

    // Details of mock mouse event input here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/MouseEvent.html

    // Create mouse pressed event.
    MouseEvent mousePressedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_PRESSED, 200, 200,
        0, 0, MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false,
        false, null);

    // Fire primary button pressed mouse event to the canvas.
    canvas.fireEvent(mousePressedEvent);

    assertEquals(whiteboardWindowController.getMouseState(), "pressed");

    // Create mouse dragged event.
    MouseEvent mouseDraggedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_DRAGGED, 200, 200,
        0, 0, MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false,
        false, null);

    // Fire primary button dragged mouse event to the canvas.
    canvas.fireEvent(mouseDraggedEvent);

    assertEquals(whiteboardWindowController.getMouseState(), "dragged");

    // Create mouse release event.
    MouseEvent mouseReleasedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_RELEASED, 200,
        200, 0, 0, MouseButton.PRIMARY, 0, false, false, false, false, false, false, false, false,
        false, false, null);

    // Fire primary button released mouse event to the canvas.
    canvas.fireEvent(mouseReleasedEvent);

    assertEquals(whiteboardWindowController.getMouseState(), "released");
  }
}
