package application.controller;

import application.controller.enums.WhiteboardRenderResult;
import application.controller.enums.WhiteboardRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.WhiteboardRequestService;
import application.controller.services.WhiteboardService;
import application.model.Whiteboard;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
    this.userID = userID;
    this.sessionID = sessionID;
    sendRequest();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.whiteboard = new Whiteboard(canvas, canvasTemp);
    this.canvasTool = "pen";
    addActionListeners();
    log.info("Whiteboard Initialised.");
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
        setStrokeColor(colorPicker.getValue());
        setStrokeWidth((int) widthSlider.getValue());
        // ... set the start coordinates.
        whiteboard.setStartPosition(mouseEvent);

        if (penButton.isSelected()) {
          // ... start a new path.
          whiteboard.createNewStroke();
          canvasTool = "pen";

        } else if (highlighterButton.isSelected()) {
          // ... set the start coordinates of the line.
          whiteboard.startLine(mouseEvent);
          canvasTool = "highlighter";

        } else if (eraserButton.isSelected()) {
          // ... start a new path.
          whiteboard.createNewStroke();
          canvasTool = "eraser";

        } else if (squareButton.isSelected()) {
          canvasTool = "square";

        } else if (circleButton.isSelected()) {
          canvasTool = "circle";

        } else if (lineButton.isSelected()) {
          // ... set the start coordinates of the line.
          whiteboard.startLine(mouseEvent);
          canvasTool = "line";

        } else if (textButton.isSelected()) {
          canvasTool = "text";
        }

        // Send package to server.
        sendPackage(mouseEvent);
        // TODO - Anchor Point
      }
    });

    // Add mouse dragged action listener to canvas.
    canvas.setOnMouseDragged(mouseEvent -> {
      // If primary mouse button is down...
      if (mouseEvent.isPrimaryButtonDown()) {
        // ... set the state of the mouse to active, ...
        mouseState = "active";
        canvasTemp.toFront();

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

        } else if (textButton.isSelected()) {
          setStrokeWidth(1);
          setStrokeColor(colorPickerText.getValue());
          // .. draw preview text on the temp canvas
          whiteboard.drawTextEffect(textField.getText(), mouseEvent);
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
        canvasTemp.toBack();

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

        } else if (textButton.isSelected()) {
          // ... draw the text
          whiteboard.drawText(textField.getText(), mouseEvent);
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
    Point2D strokePos = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    whiteboardService.createSessionPackage(mouseState, canvasTool, whiteboard.getStrokeColor(),
        whiteboard.getStrokeWidth(), strokePos, strokePos);
    // TODO - Anchor Point

    if (!whiteboardService.isRunning()) {
      whiteboardService.reset();
      whiteboardService.start();
    }

    whiteboardService.setOnSucceeded(event -> {
      WhiteboardRenderResult result = whiteboardService.getValue();
      switch (result) {
        case WHITEBOARD_RENDER_SUCCESS:
          log.info("Whiteboard Session Package - Received.");
          break;
        case FAILED_BY_INCORRECT_USER_ID:
          log.warn("Whiteboard Session Package - Wrong user ID.");
          break;
        case FAILED_BY_UNEXPECTED_ERROR:
          log.warn("Whiteboard Session Package - Unexpected error.");
          break;
        case FAILED_BY_NETWORK:
          log.warn("Whiteboard Session Package - Network error.");
          break;
        default:
          log.warn("Whiteboard Session Package - Unknown error.");
      }
    });
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
          whiteboardService.start();
          listenForUpdates();
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

  private void listenForUpdates() {
    // TODO - loop on the second DOS, when update is found, apply to client.
    while(true) {
      try {
        GraphicsContext gc = connection.listenForWhiteboard();
        whiteboard.setGraphicsContext(gc);
      } catch (IOException e) {
        log.warn(e.toString());
      }
    }
  }

  /* SETTERS and GETTERS */

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
