package application.controller.services;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class ImageHandler {
    private Canvas targetCanvas = null;
    private ArrayList<Canvas> images = new ArrayList<Canvas>();

    public ImageHandler(Canvas targetCanvas) {
        this.targetCanvas = targetCanvas;
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
        Canvas newLayer = new Canvas(targetCanvas.getHeight(), targetCanvas.getWidth());
        images.add(newLayer);
    }

}
