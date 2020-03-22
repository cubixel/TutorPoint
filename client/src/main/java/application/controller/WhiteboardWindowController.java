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

  @FXML
  private Canvas canvas;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  @FXML
  private ToggleButton penButton, eraserButton, squareButton, circleButton, lineButton;

  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String tutorID) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboardService = new WhiteboardService(mainConnection, tutorID);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.whiteboard = new Whiteboard(canvas);
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
    addActionListeners();
  }

  /**
   * Method to initialise the main whiteboard action listeners to the components.
   */
  private void addActionListeners() {

    //inactivityTimer = new Timer("inactivity", false);

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

      if (penButton.isSelected()) {
        // If primary mouse button is down...
        if (mouseEvent.isPrimaryButtonDown()) {

          // ... set the state of the mouse to active, ...
          mouseState = "active";

          // ... start a new path.
          whiteboard.createNewStroke();

          // Send package to server.
          sendPackage(mouseEvent);
        }
      }

      else if (eraserButton.isSelected()) {
        if (mouseEvent.isPrimaryButtonDown()) {
          // ... start a new path.
          whiteboard.createNewStroke();
        }
      }

      else if (lineButton.isSelected()){
        if (mouseEvent.isPrimaryButtonDown()) {
          // ... sets the start coordinates of the line.
          whiteboard.startLine(mouseEvent);
        }
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {

      if (penButton.isSelected()) {
        // If primary mouse button is down...
        if (mouseEvent.isPrimaryButtonDown()) {

          // ... set the state of the mouse to active, ...
          mouseState = "active";

          setStrokeColor(colorPicker.getValue());

          // ... draw a new path.
          whiteboard.draw(mouseEvent);

          // Send package to server.
          sendPackage(mouseEvent);
        }
      }

      else if (eraserButton.isSelected()) {
        if (mouseEvent.isPrimaryButtonDown()) {
          // ... draw a new white path.
          whiteboard.erase(mouseEvent);
        }
      }

      else if (lineButton.isSelected()){
        if (mouseEvent.isPrimaryButtonDown()) {
          setStrokeColor(colorPicker.getValue());
          // ... sets the end coordinates of the line.
          whiteboard.endLine(mouseEvent);
        }
      }

    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {

      if (penButton.isSelected()) {
        // If primary mouse button is released...
        if (!mouseEvent.isPrimaryButtonDown()) {

          // ... set the state of the mouse to idle, ...
          mouseState = "active";

          // ... end path.
          whiteboard.endNewStroke();

          // Send package to server.
          sendPackage(mouseEvent);
        }
      }

      else if (eraserButton.isSelected()) {
        if (!mouseEvent.isPrimaryButtonDown()) {
          // ... end path.
          whiteboard.endNewStroke();
        }
      }

      else if (lineButton.isSelected()){
        if (!mouseEvent.isPrimaryButtonDown()) {
          // ... draws the line.
          whiteboard.drawLine();
        }
      }

    });
  }

  public void sendPackage(MouseEvent mouseEvent) {
    whiteboardService.createSessionPackage(mouseState, whiteboard.getStrokeColor(),
        whiteboard.getStrokeWidth(), mouseEvent.getX(), mouseEvent.getY());

    whiteboardService.start();
    whiteboardService.setOnSucceeded(event -> {
      WhiteboardRenderResult result = whiteboardService.getValue();
      switch (result) {
        case SUCCESS:
          System.out.println("Package Sent");
          break;
        case FAILED_BY_INCORRECT_TUTOR_ID:
          System.out.println("Wrong Tutor ID");
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

  // TODO - Use ID of icon to pass to model whiteboard.setTool('icon-id');
  @FXML
  void selectTool(MouseEvent event) {
    System.out.println(event.getSource());
    setStrokeTool("pen");
  }

  public void setStrokeTool(String tool) {
    whiteboard.setStrokeTool(tool);
  }

  public void setStrokeWidth(int value) {
    whiteboard.setStrokeWidth(value);

    //TODO - Send widthSlider.getValue() to WhiteboardService.
  }

  public void setStrokeColor(Color color) {
    whiteboard.setStrokeColor(color);

    //TODO - Send colorPicker.getValue() to WhiteboardService.
  }

  public String getMouseState() {
    return mouseState;
  }
}
