package application.controller.services;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class ImageHandler {

    private StackPane pane;
    private Canvas newCanvas;
    private Image image;

    public ImageHandler(StackPane targetPane, String url, int x, int y, int w, int h) {
        this.pane = targetPane;
        this.image = new Image(url, w, h, true, true);

        newCanvas = new Canvas((double) w + x, (double) h + y);

        GraphicsContext gc = newCanvas.getGraphicsContext2D();
        gc.drawImage(image, x, y);

        pane.getChildren().add(newCanvas);
    }

    public void remove(){
        pane.getChildren().remove(newCanvas);
        this.newCanvas = null;
        this.pane = null;
    }
}
