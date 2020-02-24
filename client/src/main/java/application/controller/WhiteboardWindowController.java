package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Objects;

public class WhiteboardWindowController extends BaseController {

    @FXML
    private Canvas whiteboard;

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

    private ArrayList<Objects> strokes;

    public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
        initWhiteboard();
    }

    /*
    public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection, Canvas canvas, Slider widthSlider, ColorPicker colorPicker) {
        super(viewFactory, fxmlName, mainConnection);
        this.whiteboard = canvas;
        this.widthSlider = widthSlider;
        this.colorPicker = colorPicker;
        initWhiteboard();
    }
    */

    private void initWhiteboard() {

        whiteboard = new Canvas();
        widthSlider = new Slider();
        colorPicker = new ColorPicker();

        gc = whiteboard.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        // Set the canvas height and width.
        whiteboard.setHeight(790);
        whiteboard.setWidth(1200);

        widthSlider.valueProperty().addListener(mouseEvent-> {
            setPenWidth(widthSlider.getValue());
        });

        colorPicker.setOnAction(mouseEvent-> {
            setPenColor(colorPicker.getValue());
        });

        whiteboard.setOnMousePressed(mouseEvent -> {
            createNewStroke(mouseEvent);
        });

        whiteboard.setOnMouseDragged(mouseEvent -> {
            draw(mouseEvent);
        });

        // TODO: Drawing lines on canvas.
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
        return whiteboard;
    }

    public void createNewStroke(MouseEvent mouseEvent) {

        // If primary mouse button isn't down, ignore and return.
        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }

        // If primary mouse button is down, create new stroke path.
        gc.beginPath();
    }

    public void draw(MouseEvent mouseEvent) {

        // If primary mouse button isn't down, ignore and return.
        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }

        gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
        gc.stroke();
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



    /*
    public void update() {
        // Package new stroke line and send to server.
    }
     */

}
