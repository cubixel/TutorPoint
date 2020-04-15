package application.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the interactive whiteboard model containing
 * all draw functions. The model as used via a universal
 * 'draw()' function that can be called by the controller
 * locally and by the service remotely.
 *
 * @author Oliver Still
 * @author Cameron Smith
 */
public class Whiteboard {

  private Canvas canvas;
  private GraphicsContext gc;
  private GraphicsContext gcTemp;
  private Point2D anchorPos;
  private Line line;
  private String prevMouseState;
  private String textField;
  private Color textColor;
  private static final Logger log = LoggerFactory.getLogger("Whiteboard");

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
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setMiterLimit(1);

    // Set the color and stroke width of the tool.
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(10);
    gcTemp.setStroke(Color.BLACK);
    gcTemp.setLineWidth(10);

    // Set the canvas and temp canvas height and width.
    canvas.setHeight(790);
    canvas.setWidth(1200);
    canvasTemp.setHeight(790);
    canvasTemp.setWidth(1200);

    // Initialise class variables.
    line = new Line();
    anchorPos = new Point2D(0,0);
    textField = "";
    textColor = Color.BLACK;
    prevMouseState = "idle";
  }

  /**
   * Top level function for drawing to the whiteboard
   * canvas.
   *
   * @param canvasTool The selected tool name.
   * @param mouseState The state of the client's mouse ('idle'/'active').
   * @param mousePos The 2D coordinates of the mouse on the canvas.
   */
  public void draw(String canvasTool, String mouseState, Point2D mousePos) {

    // Mouse has been pressed:
    if (prevMouseState.equals("idle") && mouseState.equals("active")) {

      // Set anchor point for lines and shapes.
      anchorPos = mousePos;

      // Create new stroke for pens.
      if (canvasTool.equals("pen") || canvasTool.equals("eraser")) {
        // ... start a new path.
        createNewStroke();

        // Start line position for highlighter and line shape.
      } else if (canvasTool.equals("highlighter") || canvasTool.equals("line")) {
        // ... set the start coordinates of the line.
        startLine(mousePos);
      }

      // Mouse is being dragged:
    } else if (prevMouseState.equals("active") && mouseState.equals("active")) {

      switch (canvasTool) {
        case "pen":
          // ... draw a new coloured path.
          drawPen(mousePos);
          break;

        case "highlighter":
          // ... draw preview line on the temp canvas.
          highlightEffect(mousePos);
          endLine(mousePos); // TODO - Possible to remove?
          break;

        case "eraser":
          // ... draw a new white path.
          setStrokeColor(Color.WHITE);
          drawPen(mousePos);
          break;

        case "square":
          // ... draw preview square on the temp canvas.
          drawRectEffect(mousePos);
          break;

        case "circle":
          // ... draw preview circle on the temp canvas.
          drawCircEffect(mousePos);
          break;

        case "line":
          // ... draw preview line on the temp canvas
          drawLineEffect(mousePos);
          endLine(mousePos); // TODO - Possible to remove?
          break;

        case "text":
          // .. draw preview text on the temp canvas
          drawTextEffect(mousePos);
          break;

        default:
          log.warn("Canvas tool not valid.");
          break;
      }

      // Mouse has been released:
    } else if (prevMouseState.equals("active") && mouseState.equals("idle")) {

      switch (canvasTool) {
        case "pen":
          // ... end path.
          endNewStroke();
          break;

        case "highlighter":
          // ... draw the line.
          highlight();
          break;

        case "eraser":
          // ... end path.
          endNewStroke();
          break;

        case "square":
          // ... draw the square.
          drawRect(mousePos);
          break;

        case "circle":
          // ... draw the circle.
          drawCirc(mousePos);
          break;

        case "line":
          // ... draw the line.
          drawLine();
          break;

        case "text":
          // ... draw the text
          drawText(mousePos);
          break;

        default:
          log.warn("Canvas tool not valid.");
          break;
      }
    }
    // Store mouse state to compare on next call.
    prevMouseState = mouseState;
  }

  /**
   * Begins a new graphics context path when the primary mouse button is pressed.
   * Updates the state of the mouse to 'pressed'.
   */
  private void createNewStroke() {
    gc.beginPath();
    log.info("Start of new stroke.");
  }

  /**
   * Continues the new graphics context path when the
   * primary mouse button is dragged.
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void drawPen(Point2D mousePos) {
    gc.lineTo(mousePos.getX(), mousePos.getY());
    gc.stroke();
  }

  /**
   * Ends the new graphics context path when the primary mouse button is released.
   * Updates the state of the mouse to 'released'.
   */
  private void endNewStroke() {
    gc.closePath();
    log.info("End of new stroke.");
  }

  /**
   * Sets the start coordinates for a new line.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void startLine(Point2D mousePos) {
    line.setStartX(mousePos.getX());
    line.setStartY(mousePos.getY());
  }

  /**
   * Sets the end coordinates for a new line.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void endLine(Point2D mousePos) {
    line.setEndX(mousePos.getX());
    line.setEndY(mousePos.getY());
  }

  /**
   * Draws an opaque line onto the canvas.
   */
  private void highlight() {
    gcTemp.clearRect(0,0,1200,790);
    // Set opacity to 40%
    gc.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 0.4));
    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
  }

  /**
   * Draws a preview opaque line onto a temp canvas.
   */
  private void highlightEffect(Point2D mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    // Set opacity to 40%
    gcTemp.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 0.4));
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeLine(line.getStartX(), line.getStartY(), mouseEvent.getX(), mouseEvent.getY());
  }

  /**
   * Draws the new line using the start and end coordinates.
   */
  private void drawLine() {
    gcTemp.clearRect(0,0,1200,790);
    // Set opacity to 0%
    gc.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 1));
    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
  }

  /**
   * Draws a preview line onto a temp canvas.
   */
  private void drawLineEffect(Point2D mouseEvent) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    // Sets opacity to 100%
    gcTemp.setStroke(Color.color(getStrokeColor().getRed(), getStrokeColor().getGreen(),
        getStrokeColor().getBlue(), 1));
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeLine(line.getStartX(), line.getStartY(), mouseEvent.getX(), mouseEvent.getY());
  }

  /**
   * Draws the new rectangle using the start
   * coordinates, height and width.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void drawRect(Point2D mousePos) {
    gcTemp.clearRect(0,0,1200,790);
    gc.strokeRect(Math.min(anchorPos.getX(), mousePos.getX()),
        Math.min(anchorPos.getY(), mousePos.getY()),
        Math.abs(anchorPos.getX() - mousePos.getX()),
        Math.abs(anchorPos.getY() - mousePos.getY()));
  }

  /**
   * Function to show the rectangle size and position before placing on canvas.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void drawRectEffect(Point2D mousePos) {
    gcTemp.setLineWidth(getStrokeWidth());
    gcTemp.setStroke(getStrokeColor());
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeRect(Math.min(anchorPos.getX(), mousePos.getX()),
        Math.min(anchorPos.getY(), mousePos.getY()),
        Math.abs(anchorPos.getX() - mousePos.getX()),
        Math.abs(anchorPos.getY() - mousePos.getY()));
  }

  /**
   * Draws the new circle using the start coordinates and radius.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void drawCirc(Point2D mousePos) {
    gcTemp.clearRect(0,0,1200,790);
    gc.strokeOval(Math.min(anchorPos.getX(), mousePos.getX()),
        Math.min(anchorPos.getY(), mousePos.getY()),
        Math.abs(anchorPos.getX() - mousePos.getX()),
        Math.abs(anchorPos.getY() - mousePos.getY()));
  }

  /**
   * Function to show the circle size and position before placing on canvas.
   *
   * @param mousePos 2D coordinates of the mouse pointer.
   */
  private void drawCircEffect(Point2D mousePos) {
    gcTemp.setLineCap(StrokeLineCap.ROUND);
    gcTemp.setLineWidth(getStrokeWidth());
    gcTemp.setStroke(getStrokeColor());
    gcTemp.clearRect(0,0,1200,790);
    gcTemp.strokeOval(Math.min(anchorPos.getX(), mousePos.getX()),
        Math.min(anchorPos.getY(), mousePos.getY()),
        Math.abs(anchorPos.getX() - mousePos.getX()),
        Math.abs(anchorPos.getY() - mousePos.getY()));
  }

  /**
   * Draws a new text object using the start and end coordinates.
   */
  private void drawText(Point2D mousePos) {
    gcTemp.clearRect(0,0,1200,790);

    gc.setFont(Font.font(Math.sqrt((Math.pow((mousePos.getX() - anchorPos.getX()), 2))
        + Math.pow((mousePos.getY() - anchorPos.getY()), 2)) / 2));
    gc.setFill(textColor);
    gc.setStroke(textColor);
    gc.setLineWidth(1);

    gc.fillText(textField, Math.min(anchorPos.getX(), mousePos.getX()),
        Math.max(anchorPos.getY(), mousePos.getY()));

  }

  /**
   * Draws a preview text object onto a temp canvas.
   */
  private void drawTextEffect(Point2D mousePos) {
    gcTemp.setFont(Font.font(Math.sqrt((Math.pow((mousePos.getX() - anchorPos.getX()), 2))
        + Math.pow((mousePos.getY() - anchorPos.getY()), 2)) / 2));
    gcTemp.setFill(textColor);
    gcTemp.setStroke(textColor);
    gcTemp.setLineWidth(1);

    gcTemp.clearRect(0,0,1200,790);
    gcTemp.fillText(textField, Math.min(anchorPos.getX(), mousePos.getX()),
        Math.max(anchorPos.getY(), mousePos.getY()));
  }

  /* Setters and Getters */

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

  public String getTextField() {
    return textField;
  }

  public Color getTextColor() {
    return textColor;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }

  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }
}



