package application.controller.services;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class ImageHandler {

    private Pane targetPane = null;
    private Canvas newLayer = null;
    private ArrayList<Canvas> images = new ArrayList<Canvas>();

    public ImageHandler(Pane targetPane) {
        this.targetPane = targetPane;
    }

    public int displayImage(String url, int x, int y, int w, int h)
    {
        int i = images.size();
        createLayer();
        
        Image sourceFile = new Image(url);
        GraphicsContext gc = images.get(i).getGraphicsContext2D();
        gc.drawImage(sourceFile, x, y, w, h);

        return i;
    }

    public void deleteImage(int id)
    {
        Canvas toDelete = images.get(id);
        GraphicsContext gc = toDelete.getGraphicsContext2D();
        gc.clearRect(0, 0, toDelete.getWidth(), toDelete.getHeight());
    }

    private void createLayer()
    {
        newLayer = new Canvas(targetPane.getHeight(), targetPane.getWidth());
        images.add(newLayer);
        targetPane.getChildren().add(newLayer);
        newLayer.toFront();
    }

}
