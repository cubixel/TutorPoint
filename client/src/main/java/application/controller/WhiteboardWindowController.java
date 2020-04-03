package application.controller;

import application.controller.enums.WhiteboardRenderResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

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
  private String canvasTool;

  @FXML
  private Canvas canvas;

  @FXML
  private Canvas canvasTemp;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  @FXML
  private ToggleButton penButton;

  @FXML
  private ToggleButton highlighterButton;

  @FXML
  private ToggleButton eraserButton;

  @FXML
  private ToggleButton squareButton;

  @FXML
  private ToggleButton circleButton;

  @FXML
  private ToggleButton lineButton;

  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String userID) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = new Whiteboard(canvas, canvasTemp);
    this.whiteboardService = new WhiteboardService(mainConnection, whiteboard, userID);
    canvasTool = "pen";
    whiteboardService.start();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    addActionListeners();
  }

  /**
   * Class constructor for unit testing.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Whiteboard whiteboard, WhiteboardService whiteboardService,
      ColorPicker colorPicker, Slider widthSlider) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = whiteboard;
    this.canvas = whiteboard.getCanvas();
    this.whiteboardService = whiteboardService;
    this.colorPicker = colorPicker;
    this.widthSlider = widthSlider;
    canvasTool = "pen";
    addActionListeners();
    //whiteboardService.start();
  }

  /**
   * Method to initialise the main whiteboard action listeners to the components.
   */
  private void addActionListeners() {

    // Set the state of the mouse to idle.
    mouseState = "idle";

    // Add action listener to width slider.
    widthSlider.valueProperty().addListener(mouseEvent -> {
      // Set the stroke width using the slider.
      setStrokeWidth((int) widthSlider.getValue());
    });

    // Add action listener to color picker.
    colorPicker.setOnAction(mouseEvent -> {
      // Set the stroke color using the color picker.
      setStrokeColor(colorPicker.getValue());
    });

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {

      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state of the mouse to active, ...
        mouseState = "active";

        if (penButton.isSelected()) {
          setStrokeColor(colorPicker.getValue());
          // ... start a new path.
          whiteboard.createNewStroke();
          canvasTool = "pen";

        } else if (highlighterButton.isSelected()) {
          setStrokeColor(colorPicker.getValue());
          // ... set the start coordinates of the line.
          whiteboard.startLine(mouseEvent);
          canvasTool = "highlighter";

        } else if (eraserButton.isSelected()) {
          setStrokeColor(Color.WHITE);
          // ... start a new path.
          whiteboard.createNewStroke();
          canvasTool = "eraser";

        } else if (squareButton.isSelected()) {
            setStrokeColor(colorPicker.getValue());
            // ... set the start coordinates of the square.
            whiteboard.startRect(mouseEvent);
            canvasTool = "square";

        } else if (circleButton.isSelected()) {
          setStrokeColor(colorPicker.getValue());
          // ... set the start coordinates of the circle.
          whiteboard.startCirc(mouseEvent);
          canvasTool = "circle";

        } else if (lineButton.isSelected()) {
          setStrokeColor(colorPicker.getValue());
          // ... set the start coordinates of the line.
          whiteboard.startLine(mouseEvent);
          canvasTool = "line";
        }
        // Send package to server.
        sendPackage(mouseEvent);
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {

      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state of the mouse to active, ...
        mouseState = "active";

        if (penButton.isSelected()) {
          // ... draw a new path.
          whiteboard.draw(mouseEvent);

        } else if (highlighterButton.isSelected()) {
          // ... draw preview line on the temp canvas
          whiteboard.highlightEffect(mouseEvent);
          // ... sets the end coordinates of the line.
          whiteboard.endLine(mouseEvent);

        } else if (eraserButton.isSelected()) {
          // ... draw a new white path.
          whiteboard.erase(mouseEvent);

        } else if (squareButton.isSelected()) {
          // ... draw preview square on the temp canvas.
          whiteboard.drawRectEffect(mouseEvent);

        } else if (circleButton.isSelected()) {
          // ... draw preview circle on the temp canvas.
          whiteboard.drawCircEffect(mouseEvent);

        } else if (lineButton.isSelected()) {
          // ... draw preview line on the temp canvas
          whiteboard.drawLineEffect(mouseEvent);
          // ... set the end coordinates of the line.
          whiteboard.endLine(mouseEvent);
        }

        // Send package to server.
        sendPackage(mouseEvent);
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {

      // If primary mouse button is released...
      if (!mouseEvent.isPrimaryButtonDown()) {
        // ... set the state of the mouse to idle, ...
        mouseState = "idle";

        if (penButton.isSelected()) {
          // ... end path.
          whiteboard.endNewStroke();

        } else if (highlighterButton.isSelected()) {
          // ... draw the line.
          whiteboard.highlight();

        } else if (eraserButton.isSelected()) {
          // ... end path.
          whiteboard.endNewStroke();

        } else if (squareButton.isSelected()) {
          // ... draw the square.
          whiteboard.drawRect(mouseEvent);

        } else if (circleButton.isSelected()) {
          // ... draw the circle.
          whiteboard.drawCirc(mouseEvent);

        } else if (lineButton.isSelected()) {
          // ... draw the line.
          whiteboard.drawLine();
        }

        // Send package to server.
        sendPackage(mouseEvent);
      }
    });
  }

  /**
   * Creates and sends a session package for the local whiteboard to the server whiteboard handler.
   * @param mouseEvent User input.
   */
  public void sendPackage(MouseEvent mouseEvent) {
    whiteboardService.createSessionPackage(mouseState, canvasTool, whiteboard.getStrokeColor(),
        whiteboard.getStrokeWidth(), mouseEvent.getX(), mouseEvent.getY());

    if (!whiteboardService.isRunning()) {
      whiteboardService.reset();
      whiteboardService.start();
    } else {
      System.out.println("Error as whiteboardService is still running.");
    }

    whiteboardService.setOnSucceeded(event -> {
      WhiteboardRenderResult result = whiteboardService.getValue();
      switch (result) {
        case SUCCESS:
          System.out.println("Package Successful");
          break;
        case FAILED_BY_INCORRECT_USER_ID:
          System.out.println("Wrong User ID");
          break;
        case FAILED_BY_UNEXPECTED_ERROR:
          System.out.println("Unexpected Error");
          break;
        case FAILED_BY_NETWORK:
          System.out.println("Network Error");
          break;
        default:
          System.out.println("Unknown Error");
      }
    });
  }

  /* SETTERS and GETTERS */

  @FXML
  void selectTool(MouseEvent event) {
    System.out.println(event.getSource());
  }

  public void setStrokeWidth(int value) {
    whiteboard.setStrokeWidth(value);
  }

  public void setStrokeColor(Color color) {
    whiteboard.setStrokeColor(color);
  }

  public String getMouseState() {
    return mouseState;
  }
}
