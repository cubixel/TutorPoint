package application.model;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Whiteboard {

  private Canvas canvas;
  private GraphicsContext gc;
  private String selectedTool;
  private Line line = new Line();

  /**
   * @param canvas
   */
  public Whiteboard(Canvas canvas) {
    this.canvas = canvas;

    gc = canvas.getGraphicsContext2D();

    // Set the shape of the stroke
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setMiterLimit(1);

    // Set the color and stroke width of the tool.
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(10);

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
  public void createNewStroke() {
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
   * Continues the new graphics context path with a white stroke colour when the primary mouse button is dragged.
   * Updates the state of the mouse to 'dragged'.
   */
  public void erase(MouseEvent mouseEvent) {
    gc.setStroke(Color.WHITE);
    gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
    gc.stroke();

    System.out.println("Stroke xPos: " + mouseEvent.getX() + ", yPos: " + mouseEvent.getY());
  }

  /**
   * Sets the start coordinates for a new line.
   */
  public void startLine(MouseEvent mouseEvent) {
    line.setStartX(mouseEvent.getX());
    line.setStartY(mouseEvent.getY());
  }

  /**
   * Sets the end coordinates for a new line.
   */
  public void endLine(MouseEvent mouseEvent) {
    line.setEndX(mouseEvent.getX());
    line.setEndY(mouseEvent.getY());

    // TODO – Implement update/preview functionality when drawing shapes.
  }

  /**
   * Draws the new line using the start and end coordinates.
   */
  public void drawLine() {
    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
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

  public void setStrokeTool(String tool) {
    selectedTool = tool;

    System.out.println("Whiteboard tool changed to: " + tool);
  }

  public String getStrokeTool() {
    return selectedTool;
  }

  public Canvas getCanvas() {
    return canvas;
  }
}
