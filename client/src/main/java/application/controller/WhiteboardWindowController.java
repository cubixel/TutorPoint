package application.controller;

import application.controller.enums.FileDownloadResult;
import application.controller.enums.WhiteboardRenderResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.EventHandler;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
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
import javax.imageio.stream.ImageInputStream;

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

  private Whiteboard whiteboard;

  private WhiteboardService whiteboardService;

  private String mouseState;

  private Timer inactivity;

  @FXML
  private Canvas canvas;

  @FXML
  private StackPane menuPane;

  @FXML
  private VBox toolSelector;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = new Whiteboard(canvas, "000", "000");
    this.whiteboardService = new WhiteboardService(whiteboard, mainConnection);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    addActionListers();
  }

  /**
   * Class constructor for unit testing.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Whiteboard whiteboard, WhiteboardService whiteboardService,
      ColorPicker colorPicker, Slider widthSlider) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = whiteboard;
    this.whiteboardService = whiteboardService;
    this.colorPicker = colorPicker;
    this.widthSlider = widthSlider;
    addActionListers();
  }

  /**
   * Method to initialise the main whiteboard action listeners to the components.
   */
  private void addActionListers() {

    inactivity = new Timer("inactivity", false);

    // Set the state of the mouse to idle.
    mouseState = "idle";

    // Add action listener to width slider.
    widthSlider.valueProperty().addListener(mouseEvent -> {
      // Set the stroke width using the slider.
      whiteboard.setStrokeWidth(widthSlider.getValue());
    });

    // Add action listener to color picker.
    colorPicker.setOnAction(mouseEvent -> {
      // Set the stroke color using the color picker.
      whiteboard.setStrokeColor(colorPicker.getValue());
    });

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... start a new path.
        whiteboard.createNewStroke(mouseEvent);

        // Set the state of the mouse to pressed.
        mouseState = "pressed";
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... draw a new path.
        whiteboard.draw(mouseEvent);

        // Set the state of the mouse to dragged.
        mouseState = "dragged";
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
      // If primary mouse button is released...
      if (!mouseEvent.isPrimaryButtonDown()) {
        // ... end path.
        whiteboard.endNewStroke();

        // Set the state of the mouse to released.
        mouseState = "released";
      }
    });

    // Event listener to set mouse state to 'idle' after one second of inactivity.
    canvas.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        inactivity.schedule(new TimerTask() {
          @Override
          public void run() {
            mouseState = "idle";
          }
        }, 1000);
      }
    });
  }

  // TODO - Use ID of icon to pass to model whiteboard.setTool('icon-id');
  @FXML
  void selectTool(MouseEvent event) {
    whiteboard.setTool("pen");
  }

  /* SETTERS and GETTERS */

  public String getMouseState() {
    return mouseState;
  }
}
