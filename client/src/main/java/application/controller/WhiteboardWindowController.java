package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WhiteboardWindowController extends BaseController implements Initializable {

    /*
    private class CartesianCoordinate {
        private double xPos;
        private double yPos;

        public double getXPos() {
            return xPos;
        }

        public void setXPos(double xPos) {
            this.xPos = xPos;
        }

        public double getYPos() {
            return yPos;
        }

        public void setYPos(double yPos) {
            this.yPos = yPos;
        }
    }
    */

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

    //private ArrayList<CartesianCoordinate> strokes;
    private int numberOfStrokes;

    public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initWhiteboard();
    }

    public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection, Canvas canvas, Slider widthSlider, ColorPicker colorPicker) {
        // TEST CONSTRUCTOR
        super(viewFactory, fxmlName, mainConnection);
        this.canvas = canvas;
        this.widthSlider = widthSlider;
        this.colorPicker = colorPicker;
        initWhiteboard();
    }

    private void initWhiteboard() {

        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setMiterLimit(1);

        setTool("pen");
        setPenColor(Color.BLACK);
        setPenWidth(10);

        // Set the canvas height and width.
        canvas.setHeight(790);
        canvas.setWidth(1200);

        numberOfStrokes = 0;

        widthSlider.valueProperty().addListener(mouseEvent -> {
            setPenWidth(widthSlider.getValue());
        });

        colorPicker.setOnAction(mouseEvent -> {
            setPenColor(colorPicker.getValue());
        });

        canvas.setOnMousePressed(mouseEvent -> {
            // If primary mouse button is down, start new path.
            if (mouseEvent.isPrimaryButtonDown()) {
                createNewStroke(mouseEvent);
            }
        });

        canvas.setOnMouseDragged(mouseEvent -> {
            // If primary mouse button is down, start new path.
            if (mouseEvent.isPrimaryButtonDown()) {
                draw(mouseEvent);
            }
        });

        canvas.setOnMouseReleased(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                endNewStroke();
            }
        });
    }

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

    public int getNumberOfStrokes() {
        return numberOfStrokes;
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

    public void createNewStroke(MouseEvent mouseEvent) {
        gc.beginPath();

        System.out.println("Start of new stroke.");
    }

    public void draw(MouseEvent mouseEvent) {
        gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
        gc.stroke();

        System.out.println("xPos: " + mouseEvent.getX() + ", yPos: " + mouseEvent.getY());

        //updateScreen()
    }

    public void endNewStroke() {
        gc.closePath();

        numberOfStrokes++;

        System.out.println("End of new stroke.");
    }

    /*
    public void updateScreen() {
        // Package new stroke line and send to server.

    }
    */

}
