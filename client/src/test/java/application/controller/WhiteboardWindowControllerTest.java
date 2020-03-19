package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import application.controller.enums.WhiteboardRenderResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
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

  @Mock
  protected WhiteboardService whiteboardServiceMock;

  protected Whiteboard whiteboard;

  protected Slider widthSlider;

  protected ColorPicker colorPicker;

  protected WhiteboardWindowController whiteboardWindowController;

  /**
   * Tests that the whiteboard canvas has been initialised and is not null.
   */
  public void testWhiteboardInitialisation() {
    assertNotNull(whiteboard);

    System.out.println("Whiteboard Initialisation - Test Complete");
  }

  /**
   * Test the tool select getters and setters in WhiteboardWindowController.
   * Sets the tool type, before asserting the string is equal to the getter's value.
   */
  public void testSelectTool() {
    // User selects new tool.
    whiteboardWindowController.setStrokeTool("pen");

    // Check new tool is selected and active.
    assertEquals("pen", whiteboard.getStrokeTool());

    System.out.println("Tool Select - Test Complete");
  }

  /**
   * Test the penColor getters and setters in WhiteboardWindowController.
   * Sets the pen color, before asserting the value is equal to the getter's hex value.
   */
  public void testChangeColor() {
    // User selects new stroke color.
    whiteboardWindowController.setStrokeColor(Color.BLACK);

    // Check selected stroke color is active.
    assertEquals(Color.BLACK, whiteboard.getStrokeColor());

    System.out.println("Stroke Color Select - Test Complete");
  }

  /**
   * Test the penWidth getters and setters in WhiteboardWindowController.
   * Sets the pen width, before asserting the value is equal to the getter's value.
   */
  public void testChangeWidth() {
    // User selects new stroke width.
    whiteboardWindowController.setStrokeWidth(10);

    // Check selected stroke width is active.
    assertEquals(10, whiteboard.getStrokeWidth());

    System.out.println("Stroke Width Select - Test Complete");
  }

  /**
   * Test the drawLine method in WhiteboardWindowController.
   * Asserts the mouseState variable after each mouse event is fired to the canvas.
   * If all asserts pass, a new line is perceived to be drawn to the canvas.
   */
  public void testDrawLine() {

    Canvas canvas = whiteboard.getCanvas();

    // Details of mock mouse event input here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/MouseEvent.html

    // Create mouse pressed event.
    MouseEvent mousePressedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_PRESSED, 200, 200,
        0, 0, MouseButton.PRIMARY, 1, false, false, false, false,
        true, false, false,
        false, false, false, null);

    // Fire primary button pressed mouse event to the canvas.
    canvas.fireEvent(mousePressedEvent);

    System.out.println("Mouse Pressed");

    assertEquals("active", whiteboardWindowController.getMouseState());

    // Create mouse dragged event.
    MouseEvent mouseDraggedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_DRAGGED, 200, 200,
        0, 0, MouseButton.PRIMARY, 1, false, false, false, false,
        true, false, false,
        false, false, false, null);

    // Fire primary button dragged mouse event to the canvas.
    canvas.fireEvent(mouseDraggedEvent);

    System.out.println("Mouse Dragged");

    assertEquals("active", whiteboardWindowController.getMouseState());

    // Create mouse release event.
    MouseEvent mouseReleasedEvent = new MouseEvent(null, canvas, MouseEvent.MOUSE_RELEASED, 200, 200,
        0, 0, MouseButton.PRIMARY, 0, false, false, false, false,
        false, false, false,
        false, false, false, null);

    // Fire primary button released mouse event to the canvas.
    canvas.fireEvent(mouseReleasedEvent);

    System.out.println("Mouse Released");

    assertEquals("idle", whiteboardWindowController.getMouseState());
  }
}
