package application.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class Whiteboard {

  private Canvas canvas;
  private GraphicsContext gc;
  private String streamID;
  private String tutorID;
  private boolean tutorOnlyAccess;
  private String selectedTool;

  /**
   *
   * @param canvas
   * @param streamID
   * @param tutorID
   */
  public Whiteboard(Canvas canvas, String streamID, String tutorID) {
    this.canvas = canvas;
    this.streamID = streamID;
    this.tutorID = tutorID;

    tutorOnlyAccess = true;

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

    System.out.println("xPos: " + mouseEvent.getX() + ", yPos: " + mouseEvent.getY());
  }

  /**
   * Ends the new graphics context path when the primary mouse button is released.
   * Updates the state of the mouse to 'released'.
   */
  public void endNewStroke() {
    gc.closePath();

    updateCanvas();

    System.out.println("End of new stroke.");
  }

  /**
   * Snapshots and flattens all graphics context on the canvas to a single image file.
   */
  public void updateCanvas() {
    // Create upscaled blank image of scale 2.
    WritableImage image = new WritableImage((int) canvas.getWidth() * 2,
        (int) canvas.getHeight() * 2);

    // Write a snapshot of the canvas using unscaled image to a new image.
    WritableImage snapshot = canvas.snapshot(null, image);

    // Downscale and draw image to canvas' graphics context.
    gc.drawImage(snapshot, canvas.getWidth(), canvas.getWidth(), 0, 0);
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

  public String getStreamID() {
    return streamID;
  }

  public String getTutorID() {
    return tutorID;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public void setTutorOnlyAccess(boolean access) {
    this.tutorOnlyAccess = access;
  }

  public boolean isAccessTutorOnly() {
    return tutorOnlyAccess;
  }
}
