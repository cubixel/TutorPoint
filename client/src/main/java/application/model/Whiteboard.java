package application.model;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class Whiteboard {

  private Canvas canvas;
  private GraphicsContext gc;
  private GraphicsContext gcTemp;
  private Point2D rectStart;
  private Point2D rectEnd;
  private Point2D circStart;
  private Point2D circEnd;
  private Line line = new Line();

  /**
   * Model for the whiteboard screen.
   * @param canvas Main canvas that content is drawn to.
   * @param canvasTemp Canvas that content is drawn to before the main canvas.
   */
  public Whiteboard(Canvas canvas, Canvas canvasTemp) {
    this.canvas = canvas;

    // Initialise the main and temp graphics context.
    gc = canvas.getGraphicsContext2D();
    gcTemp = canvasTemp.getGraphicsContext2D();

    // Set the shape of the stroke
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setMiterLimit(1);

    // Set the color and stroke width of the tool.
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(10);

    // Set the canvas and temp canvas height and width.
    canvas.setHeight(790);
    canvas.setWidth(1200);
    canvasTemp.setHeight(790);
    canvasTemp.setWidth(1200);
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
   * Ends the new graphics context path when the primary mouse button is released.
   * Updates the state of the mouse to 'released'.
   */
  public void endNewStroke() {
    gc.closePath();
    System.out.println("End of new stroke.");
  }

  /**
   * Continues the new graphics context path when the primary mouse button is dragged.
   * Updates the state of the mouse to 'dragged'.
   */
  public void draw(MouseEvent mouseEvent) {
    gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
    gc.stroke();
  }

  /**
   * Draws an opaque line onto the canvas.
   */
  public void highlight() {
    gcTemp.clearRect(0,0,1200,790);
    // Set opacity to 40%
    gc.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 0.4));
    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
  }

  /**
   * Continues the new graphics context path with a white stroke colour when the
   *  primary mouse button is dragged.
   * Updates the state of the mouse to 'dragged'.
   */
  public void erase(MouseEvent mouseEvent) {
    gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
    gc.stroke();
  }

  /**
   * Sets the start coordinates for a new rectangle.
   */
  public void startRect(MouseEvent mouseEvent) {
    rectStart = new Point2D(mouseEvent.getX(), mouseEvent.getY());
  }

  /**
   * Sets the start coordinates for a new circle.
   */
  public void startCirc(MouseEvent mouseEvent) {
    circStart = new Point2D(mouseEvent.getX(), mouseEvent.getY());
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
  }

  /**
   * Draws the new rectangle using the start coordinates, height and width.
   */
  public void drawRect(MouseEvent mouseEvent) {
    gcTemp.clearRect(0,0,1200,790);
    rectEnd = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    gc.strokeRect(Math.min(rectStart.getX(), rectEnd.getX()),
        Math.min(rectStart.getY(), rectEnd.getY()),
        Math.abs(rectStart.getX() - rectEnd.getX()),
        Math.abs(rectStart.getY() - rectEnd.getY()));
  }

  /**
   * Draws the new circle using the start coordinates and radius.
   */
  public void drawCirc(MouseEvent mouseEvent) {
    gcTemp.clearRect(0,0,1200,790);
    circEnd = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    gc.strokeOval(Math.min(circStart.getX(), circEnd.getX()),
        Math.min(circStart.getY(), circEnd.getY()),
        Math.abs(circStart.getX() - circEnd.getX()),
        Math.abs(circStart.getY() - circEnd.getY()));
  }

  /**
   * Draws the new line using the start and end coordinates.
   */
  public void drawLine() {
    gcTemp.clearRect(0,0,1200,790);
    // Set opacity to 0%
    gc.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 1));
    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
  }

  /**
   * Draws a preview opaque line onto a temp canvas.
   */
  public void highlightEffect(MouseEvent mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    // Set opacity to 40%
    gcTemp.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 0.4));
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeLine(line.getStartX(), line.getStartY(), mouseEvent.getX(), mouseEvent.getY());
  }

  /**
   * Function to show the rectangle size and position before placing on canvas.
   * @param mouseEvent User input.
   */
  public void drawRectEffect(MouseEvent mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    gcTemp.setStroke(getStrokeColor());
    gcTemp.clearRect(0,0,1200,790);
    rectEnd = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    gcTemp.strokeRect(Math.min(rectStart.getX(), rectEnd.getX()),
        Math.min(rectStart.getY(), rectEnd.getY()),
        Math.abs(rectStart.getX() - rectEnd.getX()),
        Math.abs(rectStart.getY() - rectEnd.getY()));
  }

  /**
   * Function to show the circle size and position before placing on canvas.
   * @param mouseEvent User input.
   */
  public void drawCircEffect(MouseEvent mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    gcTemp.setStroke(getStrokeColor());
    gcTemp.clearRect(0,0,1200,790);
    circEnd = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    gcTemp.strokeOval(Math.min(circStart.getX(), circEnd.getX()),
        Math.min(circStart.getY(), circEnd.getY()),
        Math.abs(circStart.getX() - circEnd.getX()),
        Math.abs(circStart.getY() - circEnd.getY()));
  }

  /**
   * Draws a preview line onto a temp canvas.
   */
  public void drawLineEffect(MouseEvent mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    // Sets opacity to 100%
    gcTemp.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 1));
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeLine(line.getStartX(), line.getStartY(), mouseEvent.getX(), mouseEvent.getY());
  }

  public void setStrokeColor(Color color) {
    gc.setStroke(color);
  }

  public Color getStrokeColor() {
    return (Color) gc.getStroke();
  }

  public void setStrokeWidth(double width) {
    gc.setLineWidth(width);
  }

  public int getStrokeWidth() {
    return (int) gc.getLineWidth();
  }

  public Canvas getCanvas() {
    return canvas;
  }
}
