package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class WhiteboardWindowController extends BaseController {

    @FXML
    private Canvas canvas;

    @FXML
    private Pane menuPane;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider widthSlider;

    private GraphicsContext gc;

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
            createLine();
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

    public void createLine() {
        gc.beginPath();
    }

    public void draw(MouseEvent mouseEvent) {
        gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
        gc.stroke();
    }

}
