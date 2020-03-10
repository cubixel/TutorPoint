package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.EventHandler;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
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
  private MainConnection connection;
  private String tutorID;
  private String mouseState;
  private Timer inactivityTimer;

  @FXML
  private Canvas canvas;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String sessionID, String tutorID) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = mainConnection;
    this.tutorID = tutorID;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.whiteboard = new Whiteboard(canvas);
    this.whiteboardService = new WhiteboardService(connection, tutorID);
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
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... start a new path.
        whiteboard.createNewStroke(mouseEvent);

        //TODO - Send beginPath to server handler.
        //whiteboardService

        // Set the state of the mouse to pressed.
        mouseState = "pressed";
        inactivityTimer.cancel();
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... draw a new path.
        whiteboard.draw(mouseEvent);

        //TODO - Send mouse position to server handler.
        //whiteboardService

        // Set the state of the mouse to dragged.
        mouseState = "dragged";
        inactivityTimer.cancel();
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
      // If primary mouse button is released...
      if (!mouseEvent.isPrimaryButtonDown()) {
        // ... end path.
        whiteboard.endNewStroke();

        //TODO - Send closePath to server handler.
        //whiteboardService

        // Set the state of the mouse to released.
        mouseState = "released";
        inactivityTimer.cancel();
      }
    });

    /*
    // Event listener to set mouse state to 'idle' after one second of inactivity.
    canvas.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        inactivityTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            mouseState = "idle";
          }
        }, 1000);
      }
    });
     */
  }

  /* SETTERS and GETTERS */

  // TODO - Use ID of icon to pass to model whiteboard.setTool('icon-id');
  @FXML
  void selectTool(MouseEvent event) {
    System.out.println(event.getSource());
    whiteboard.setTool("pen");
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
