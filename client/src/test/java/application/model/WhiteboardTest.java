package application.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is testing the Whiteboard class. It tests the
 * constructor and confirms the fields within the
 * Whiteboard object take the values passed in and when
 * using getters and setters, as well as the drawing functionality.
 *
 * @author Oliver Still
 */
public class WhiteboardTest {

  private Whiteboard whiteboard;
  private Canvas canvas = new Canvas();
  private Canvas canvasTemp = new Canvas();

  @BeforeEach
  public void setup() {
    whiteboard = new Whiteboard(canvas, canvasTemp);
  }

  @Test
  public void constructorTest() {
    assertEquals(canvas, whiteboard.getCanvas());
  }

  @Test
  public void drawTest() {
    // Set mouse position.
    Point2D mousePos = new Point2D(10,10);

    // Simulate user pressing down on canvas.
    whiteboard.draw("highlighter", "idle", mousePos);

    assertEquals("idle", whiteboard.getPrevMouseState());

    whiteboard.draw("highlighter", "active", mousePos);

    // Assert whiteboard draw function.
    assertEquals("highlighter", whiteboard.getCanvasTool());
    assertEquals("active", whiteboard.getPrevMouseState());
    assertEquals(mousePos, whiteboard.getAnchorPos());

    whiteboard.draw("highlighter", "idle", mousePos);

    // Set mouse position.
    Point2D mouseDrag1 = new Point2D(20,20);

    // Simulate user dragging on canvas.
    whiteboard.draw("line", "idle", mouseDrag1);

    assertEquals("idle", whiteboard.getPrevMouseState());

    whiteboard.draw("line", "active", mouseDrag1);

    Point2D mouseDrag2 = new Point2D(25,25);

    whiteboard.draw("line", "active", mouseDrag2);

    assertEquals("line", whiteboard.getCanvasTool());
    assertEquals("active", whiteboard.getPrevMouseState());
    assertEquals(mouseDrag1, whiteboard.getAnchorPos());

    whiteboard.draw("line", "idle", mouseDrag2);

    assertEquals("idle", whiteboard.getPrevMouseState());
  }

  @Test
  public void textFieldTest() {
    whiteboard.setTextField("test");
    assertEquals("test", whiteboard.getTextField());
  }

}
