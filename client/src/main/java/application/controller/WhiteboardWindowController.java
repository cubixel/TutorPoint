package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used as the entrance point to generate an
 * interactive whiteboard which allows multiple users to draw
 * onto a given canvas. User can change both the color and
 * width of the tool.
 * Tools include: Pen, Shapes, Text, Eraser.
 *
 * @author Oliver Still
 * @author Cameron Smith
 * @author Che McKirgan
 */
public class WhiteboardWindowController extends BaseController implements Initializable {

  private Whiteboard whiteboard;
  private WhiteboardService whiteboardService;
  private MainConnection connection;
  private int userID;
  private int sessionID;
  private String mouseState;
  private String canvasTool;

  @FXML
  private Canvas canvas;

  @FXML
  private Canvas canvasTemp;

  @FXML
  public ColorPicker colorPicker;

  @FXML
  private Slider widthSlider;

  @FXML
  public CheckBox accessCheckBox;

  @FXML
  public ToggleButton penButton;

  @FXML
  public ToggleButton highlighterButton;

  @FXML
  public ToggleButton eraserButton;

  @FXML
  public ToggleButton squareButton;

  @FXML
  public ToggleButton circleButton;

  @FXML
  public ToggleButton lineButton;

  @FXML
  public ToggleButton textButton;

  @FXML
  public TextField textField;

  private static final Logger log = LoggerFactory.getLogger("WhiteboardWindowController");

  /**
   * Main class constructor.
   *
   * @param viewFactory Main view factory.
   * @param fxmlName FXML file name / directory.
   * @param mainConnection Main connection of client.
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, int userID, int sessionID) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    this.whiteboard = new Whiteboard(canvas, canvasTemp);
    startService();

    this.canvasTool = "pen";
    this.mouseState = "idle";
    accessCheckBox.setDisable(true);
    addActionListeners();
  }

  /**
   * Class constructor for unit testing.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Whiteboard whiteboard, WhiteboardService whiteboardService,
      ColorPicker colorPicker, Slider widthSlider, CheckBox accessCheckBox, ToggleButton penButton,
      ToggleButton highlighterButton, ToggleButton eraserButton, ToggleButton squareButton,
      ToggleButton circleButton, ToggleButton lineButton, ToggleButton textButton) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = whiteboard;
    this.canvas = whiteboard.getCanvas();
    this.canvasTemp = canvas;
    this.whiteboardService = whiteboardService;
    this.colorPicker = colorPicker;
    this.widthSlider = widthSlider;
    this.accessCheckBox = accessCheckBox;
    this.penButton = penButton;
    this.highlighterButton = highlighterButton;
    this.eraserButton = eraserButton;
    this.squareButton = squareButton;
    this.circleButton = circleButton;
    this.lineButton = lineButton;
    this.textButton = textButton;
    this.canvasTool = "pen";
    this.mouseState = "idle";
    addActionListeners();
  }

  /**
   * Method to initialise the main whiteboard action listeners to the components.
   */
  private void addActionListeners() {

    accessCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
          Boolean newValue) {
        whiteboard.setTutorOnlyAccess(newValue);
        // TODO - Possibly a bad thing to do, but otherwise access isn't updated until the
        //    tutor next sends a package.
        whiteboardService.sendSessionUpdates(newValue.toString(), "access", new Point2D(-1,-1));
      }
    });

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {
      // Check tool is selected
      updateCanvasTool();
      if (canvasTool.equals("idle")) {
        return;
      }

      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas colour and width from the GUI elements.
        updateStroke();

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendSessionUpdates(canvasTool, mouseState, mousePos);
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // Check tool is selected
      if (canvasTool.equals("idle")) {
        return;
      }

      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas overlay to front.
        canvasTemp.toFront();

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendSessionUpdates(canvasTool, mouseState, mousePos);
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
      // Check tool is selected
      if (canvasTool.equals("idle")) {
        return;
      }

      // If primary mouse button is down...
      if (!mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "idle";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas overlay to back.
        canvasTemp.toBack();

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendSessionUpdates(canvasTool, mouseState, mousePos);
      }
    });
  }

  /**
   * Updates the canvasTool variable based on what JavaFX
   * button is currently selected.
   */
  public void updateCanvasTool() {
    // Set canvas tool.
    if (penButton.isSelected()) {
      canvasTool = "pen";
    } else if (highlighterButton.isSelected()) {
      canvasTool = "highlighter";
    } else if (eraserButton.isSelected()) {
      canvasTool = "eraser";
    } else if (squareButton.isSelected()) {
      canvasTool = "square";
    } else if (circleButton.isSelected()) {
      canvasTool = "circle";
    } else if (lineButton.isSelected()) {
      canvasTool = "line";
    } else if (textButton.isSelected()) {
      canvasTool = "text";
      // Set the text and font color.
      whiteboard.setTextField(textField.getText());
    } else {
      canvasTool = "idle";
    }
  }

  public void updateStroke() {
    whiteboard.setStrokeColor(colorPicker.getValue());
    whiteboard.setStrokeWidth((int) widthSlider.getValue());
  }

  /**
   * Starts the whiteboard service to send and
   * receive session packages for mirroring.
   */
  private void startService() {
    this.whiteboardService = new WhiteboardService(connection, whiteboard, userID, sessionID);
    this.connection.getListener().setWhiteboardService(whiteboardService);
    this.whiteboardService.start();
  }

  /* SETTERS AND GETTERS */

  public String getMouseState() {
    return mouseState;
  }

  public String getCanvasTool() {
    return canvasTool;
  }

  public Slider getWidthSlider() {
    return widthSlider;
  }
}
