package application.controller;

import application.controller.enums.WhiteboardRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardRequestService;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private WhiteboardRequestService whiteboardRequestService;
  private MainConnection connection;
  private String userID;
  private String sessionID;
  private String mouseState;
  private String canvasTool;

  @FXML
  private Canvas canvas;

  @FXML
  private Canvas canvasTemp;

  @FXML
  private ColorPicker colorPicker;

  @FXML
  private ColorPicker colorPickerText;

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

  @FXML
  private ToggleButton textButton;

  @FXML
  private TextField textField;

  private static final Logger log = LoggerFactory.getLogger("WhiteboardWindowController");
  
  /**
   * Main class constructor.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String userID, String sessionID) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboardRequestService = new WhiteboardRequestService(mainConnection, userID, sessionID);
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
    sendRequest();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.whiteboard = new Whiteboard(canvas, canvasTemp);
    this.canvasTool = "pen";
    this.mouseState = "idle";
    addActionListeners();
    log.info("Whiteboard Initialised.");
    startService();
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
    mouseState = "idle";
    addActionListeners();
  }

  /**
   * Method to initialise the main whiteboard action listeners to the components.
   */
  private void addActionListeners() {

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

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
          whiteboard.setTextColor(colorPickerText.getValue());
          whiteboard.setTextField(textField.getText());
        }

        // Set canvas colour and width from the GUI elements.
        whiteboard.setStrokeColor(colorPicker.getValue());
        whiteboard.setStrokeWidth((int) widthSlider.getValue());

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendPackage(canvasTool, mouseState, mousePos);
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas overlay to front.
        canvasTemp.toFront();

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendPackage(canvasTool, mouseState, mousePos);
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas overlay to back.
        canvasTemp.toBack();

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendPackage(canvasTool, mouseState, mousePos);
      }
    });
  }

  public void startService() {
    this.whiteboardService = new WhiteboardService(this.connection, this.whiteboard, this.userID,
        this.sessionID);
    this.connection.getListener().setWhiteboardService(whiteboardService);
    this.whiteboardService.start();
  }

  public void sendRequest() {
    if (!whiteboardRequestService.isRunning()) {
      whiteboardRequestService.reset();
      whiteboardRequestService.start();
    }

    whiteboardRequestService.setOnSucceeded(event -> {
      WhiteboardRequestResult result = whiteboardRequestService.getValue();
      switch (result) {
        case WHITEBOARD_REQUEST_SUCCESS:
          log.info("Whiteboard Session Request - Received.");
          this.whiteboardService = new WhiteboardService(connection, whiteboard, userID, sessionID);
          break;
        case FAILED_BY_SESSION_ID:
          log.warn("Whiteboard Session Request - Wrong session ID.");
          break;
        case FAILED_BY_NETWORK:
          log.warn("Whiteboard Session Request - Network error.");
          break;
        default:
          log.warn("Whiteboard Session Request - Unknown error.");
      }
    });
  }
}
