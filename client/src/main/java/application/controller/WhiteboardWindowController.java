package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
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
    private Canvas canvas;

    @FXML
    private StackPane menuPane;

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

    public WhiteboardWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection, Canvas canvas, Slider widthSlider, ColorPicker colorPicker) {
        super(viewFactory, fxmlName, mainConnection);
        this.canvas = canvas;
        this.widthSlider = widthSlider;
        this.colorPicker = colorPicker;
        initWhiteboard();
    }

    private void initWhiteboard() {
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        // Set the canvas height and width.
        canvas.setHeight(790);
        canvas.setWidth(1200);

        widthSlider.valueProperty().addListener(mouseEvent->{
            setPenWidth(widthSlider.getValue());
        });

        colorPicker.setOnAction(mouseEvent-> {
            setPenColor(colorPicker.getValue());
        });

        canvas.setOnMousePressed(mouseEvent -> {
            createNewStroke(mouseEvent);
        });

        canvas.setOnMouseDragged(mouseEvent -> {
            draw(mouseEvent);
        });

        // TODO: Drawing lines on canvas.
    }

    public void setPenColor(Color color) {
        gc.setStroke(color);
    }

    public Color getPenColor() {
        return (Color) gc.getStroke();
    }

    public void setPenWidth(double width) {
        gc.setLineWidth(width);
    }

    public double getPenWidth() {
        return gc.getLineWidth();
    }

    public void setTool(String tool) {
        selectedTool = tool;
        // Update GUI?
    }

    public String getSelectedTool() {
        return selectedTool;
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



    /*
    public void update() {
        // Package new stroke line and send to server.
    }
     */

}
