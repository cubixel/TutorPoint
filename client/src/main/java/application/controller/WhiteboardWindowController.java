package application.controller;

import application.controller.enums.WhiteboardRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardRequestService;
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
  private CheckBox accessCheckBox;

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
   *
   * @param viewFactory Main view factory.
   * @param fxmlName FXML file name / directory.
   * @param mainConnection Main connection of client.
   * @param userID User ID of the client.
   * @param sessionID Session ID of the stream.
   */
  public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String userID, String sessionID) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    this.whiteboard = new Whiteboard(canvas, canvasTemp);
    startService();
    this.whiteboardRequestService = new WhiteboardRequestService(connection, userID, sessionID);
    sendRequest();
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
      ColorPicker colorPicker, Slider widthSlider) {
    super(viewFactory, fxmlName, mainConnection);
    this.whiteboard = whiteboard;
    this.canvas = whiteboard.getCanvas();
    this.whiteboardService = whiteboardService;
    this.colorPicker = colorPicker;
    this.widthSlider = widthSlider;
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
        // TODO - Possibly a bad thing todo, but otherwise access isn't updated until the tutor next sends a package.
        whiteboardService.sendSessionUpdates(newValue.toString(), "access", new Point2D(-1,-1));
      }
    });

    // Add mouse pressed action listener to canvas.
    canvas.setOnMousePressed(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state and position of the mouse.
        mouseState = "active";
        Point2D mousePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());

        // Set canvas colour and width from the GUI elements.
        whiteboard.setStrokeColor(colorPicker.getValue());
        whiteboard.setStrokeWidth((int) widthSlider.getValue());

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

        // Draw locally and send package to server.
        this.whiteboard.draw(canvasTool, mouseState, mousePos);
        this.whiteboardService.sendSessionUpdates(canvasTool, mouseState, mousePos);
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
        this.whiteboardService.sendSessionUpdates(canvasTool, mouseState, mousePos);
      }
    });

    // Add mouse released action listener to canvas.
    canvas.setOnMouseReleased(mouseEvent -> {
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

  private void sendRequest() {
    if (!whiteboardRequestService.isRunning()) {
      whiteboardRequestService.reset();
      whiteboardRequestService.start();
    }

    whiteboardRequestService.setOnSucceeded(event -> {
      WhiteboardRequestResult result = whiteboardRequestService.getValue();
      switch (result) {
        case SESSION_REQUEST_TRUE:
          log.info("Whiteboard Session Request - True.");
          break;
        case SESSION_REQUEST_FALSE:
          log.info("Whiteboard Session Request - False.");
          log.info("New Whiteboard Session Created - Session ID: " + sessionID);
          // TODO - Add new checkbox to toolbar that only the tutor can see.
          accessCheckBox.setDisable(false);
          this.whiteboardService = new WhiteboardService(connection, whiteboard, userID, sessionID);
          break;
        case FAILED_BY_NETWORK:
          log.warn("Whiteboard Session Request - Network error.");
          break;
        default:
          log.warn("Whiteboard Session Request - Unknown error.");
      }
    });
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
}
