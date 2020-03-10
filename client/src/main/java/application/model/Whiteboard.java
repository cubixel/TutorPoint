package application.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class Whiteboard {

  private Canvas canvas;
  private GraphicsContext gc;
  private String selectedTool;

  /**
   *
   * @param canvas
   */
  public Whiteboard(Canvas canvas) {
    this.canvas = canvas;

    gc = canvas.getGraphicsContext2D();

    // Set the shape of the stroke
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setMiterLimit(1);

    // Set the color and stroke width of the tool.
    setStrokeColor(Color.BLACK);
    setStrokeWidth(10);

    // Set the tool type.
    selectedTool = "pen";

    // Set the canvas height and width.
    canvas.setHeight(790);
    canvas.setWidth(1200);
  }

  /**
   * Begins a new graphics context path when the primary mouse button is pressed.
   * Updates the state of the mouse to 'pressed'.
   */
  public void createNewStroke(MouseEvent mouseEvent) {
    gc.beginPath();

    System.out.println("Start of new stroke.");
  }

  /**
   * Continues the new graphics context path when the primary mouse button is dragged.
   * Updates the state of the mouse to 'dragged'.
   */
  public void draw(MouseEvent mouseEvent) {
    gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
    gc.stroke();

    System.out.println("Stroke xPos: " + mouseEvent.getX() + ", yPos: " + mouseEvent.getY());
  }

  /**
   * Ends the new graphics context path when the primary mouse button is released.
   * Updates the state of the mouse to 'released'.
   */
  public void endNewStroke() {
    gc.closePath();

    System.out.println("End of new stroke.");
  }

  public void setStrokeColor(Color color) {
    gc.setStroke(color);

    System.out.println("Stroke colour changed to: " + color);
  }

  public Color getStrokeColor() {
    return (Color) gc.getStroke();
  }

  public void setStrokeWidth(double width) {
    gc.setLineWidth(width);

    System.out.println("Stroke width changed to: " + width);
  }

  public int getStrokeWidth() {
    return (int) gc.getLineWidth();
  }

  public void setTool(String tool) {
    selectedTool = tool;

    System.out.println("Whiteboard tool changed to: " + tool);
  }

  public String getSelectedTool() {
    return selectedTool;
  }

  public Canvas getCanvas() {
    return canvas;
  }
}
