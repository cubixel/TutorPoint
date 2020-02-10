package application.controller.services;

import java.util.Map;
import java.util.HashMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class ImageHandler {

    private StackPane pane;
    private Map<Integer, Canvas> images = new HashMap<>();

    public ImageHandler(StackPane targetPane) {
        this.pane = targetPane;
       
    }

    public int drawImage(String url, int id, int x, int y, int w, int h){
        
        Image picture = new Image(url, w, h, true, true);
        Canvas newCanvas = new Canvas((double) w + x, (double) h + y);

        GraphicsContext gc = newCanvas.getGraphicsContext2D();
        gc.drawImage(picture, x, y);

        if(images.putIfAbsent(id, newCanvas) == null){
            pane.getChildren().add(newCanvas);
            return id;
        } else {
            return -1;
        }
    }

    public boolean remove(int id){
        if (images.containsKey(id)){
            pane.getChildren().remove(images.get(id));
            images.remove(id);
            return true;
        } else {
            return false;
        }
        
    }
}
