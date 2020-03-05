package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Transform;

/**
 * CLASS DESCRIPTION:
 * This class is used to generate an interactive whiteboard
 * which allows multiple users to draw onto a given canvas.
 * Tools include: Pen, Shapes, Text, Eraser.
 * User can change both the color and width of the tool.
 * Scene is initialised via SceneBuilder.
 *
 * @author CUBIXEL
 *
 */

public class WhiteboardWindowController extends BaseController implements Initializable {

  @FXML
  private Canvas canvas;

  @FXML
  private StackPane menuPane;

  @FXML
  private VBox toolSelector;

  @FXML
  private ImageView penIcon;

  @FXML
  private ImageView shapeIcon;

  @FXML
  private ImageView textIcon;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  private GraphicsContext gc;

  private String selectedTool;

  private String mouseState;

  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    initWhiteboard();
  }

  /**
   * Class constructor for unit testing.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Canvas canvas, Slider widthSlider, ColorPicker colorPicker) {
    super(viewFactory, fxmlName, mainConnection);
    this.canvas = canvas;
    this.widthSlider = widthSlider;
    this.colorPicker = colorPicker;
    initWhiteboard();
  }

  /**
   * Method to initialise the main whiteboard variables,
   * whiteboard canvas with 2D graphics context,
   * and action listeners to the components.
   */
  private void initWhiteboard() {

    gc = canvas.getGraphicsContext2D();

    // Set the shape of the stroke
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setMiterLimit(1);

    // Set the tool type.
    setTool("pen");

    // Set the color and stroke width of the tool.
    setPenColor(Color.BLACK);
    setPenWidth(10);

    // Set the canvas height and width.
    canvas.setHeight(790);
    canvas.setWidth(1200);

    updateWhiteboard();

    // Set the state of the mouse to idle.
    mouseState = "idle"; // TODO - Set back to idle after released.

    // Add action listener to width slider.
    widthSlider.valueProperty().addListener(mouseEvent -> {
      // Set the stroke width using the slider.
      setPenWidth(widthSlider.getValue());
    });

    // Add action listener to color picker.
    colorPicker.setOnAction(mouseEvent -> {
      // Set the stroke color using the color picker.
      setPenColor(colorPicker.getValue());
    });

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {
      // If primary mouse button is down, start new path.
      if (mouseEvent.isPrimaryButtonDown()) {
        createNewStroke(mouseEvent);
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // If primary mouse button is down, draw new path.
      if (mouseEvent.isPrimaryButtonDown()) {
        draw(mouseEvent);
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
      // If primary mouse button is released, end new path.
      if (!mouseEvent.isPrimaryButtonDown()) {
        endNewStroke();
      }
    });
  }

  @FXML
  void selectPen(MouseEvent event) {
    setTool("pen");
    penIcon.setOpacity(0.6);
    shapeIcon.setOpacity(1.0);
    textIcon.setOpacity(1.0);
  }

  @FXML
  void selectShape(MouseEvent event) {
    setTool("shape");
    penIcon.setOpacity(1.0);
    shapeIcon.setOpacity(0.6);
    textIcon.setOpacity(1.0);
  }

  @FXML
  void selectText(MouseEvent event) {
    setTool("text");
    penIcon.setOpacity(1.0);
    shapeIcon.setOpacity(1.0);
    textIcon.setOpacity(0.6);
  }

  /**
   * Begins a new graphics context path when the primary mouse button is pressed.
   * Updates the state of the mouse to 'pressed'.
   */
  public void createNewStroke(MouseEvent mouseEvent) {
    gc.beginPath();

    // Set the state of the mouse to pressed.
    mouseState = "pressed";

    System.out.println("Start of new stroke.");
  }

  /**
   * Continues the new graphics context path when the primary mouse button is dragged.
   * Updates the state of the mouse to 'dragged'.
   */
  public void draw(MouseEvent mouseEvent) {
    gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
    gc.stroke();

    updateWhiteboard();

    // Set the state of the mouse to dragged.
    mouseState = "dragged";

    System.out.println("xPos: " + mouseEvent.getX() + ", yPos: " + mouseEvent.getY());
  }

  /**
   * Ends the new graphics context path when the primary mouse button is released.
   * Updates the state of the mouse to 'released'.
   */
  public void endNewStroke() {
    gc.closePath();

    // Set the state of the mouse to released.
    mouseState = "released";

    System.out.println("End of new stroke.");
  }

  /**
   * Snapshots and flattens all graphics context on the canvas to a single image file.
   */
  private void updateWhiteboard() {
    // Create upscaled blank image of scale 2.
    WritableImage image = new WritableImage((int) canvas.getWidth() * 2, (int) canvas.getHeight() * 2);

    // Write a snapshot of the canvas using unscaled image to a new image.
    WritableImage snapshot = canvas.snapshot(null, image);

    // Downsclae and draw image to canvas' graphics context.
    gc.drawImage(snapshot, canvas.getWidth(), canvas.getWidth(), 0, 0);

    // TODO - Serialise to Json object.
  }

  /* SETTERS and GETTERS */

  public void setPenColor(Color color) {
    gc.setStroke(color);
    System.out.println("Stroke colour changed to: " + color);
  }

  public Color getPenColor() {
    return (Color) gc.getStroke();
  }

  public void setPenWidth(double width) {
    gc.setLineWidth(width);
    System.out.println("Stroke width changed to: " + width);
  }

  public double getPenWidth() {
    return gc.getLineWidth();
  }

  public void setTool(String tool) {
    selectedTool = tool;
    System.out.println("Whiteboard tool changed to: " + tool);
  }

  public String getSelectedTool() {
    return selectedTool;
  }

  public Canvas getWhiteboard() {
    return canvas;
  }

  public String getMouseState() {
    return mouseState;
  }
}
