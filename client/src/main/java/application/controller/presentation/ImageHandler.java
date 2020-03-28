package application.controller.presentation;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * Module to draw image files onto a StackPlane by adding extra canvas.
 * Also handles deleting images via an ID assigned when drawn.
 *
 * @author Daniel Bishop
 *
 */
public class ImageHandler {

  private StackPane pane;
  private Map<String, Canvas> images = new HashMap<>();

  public ImageHandler(StackPane targetPane) {
    this.pane = targetPane;
  }

  /**
   * Registers an image from a URL onto a canvas, using the provided ID, dimensions and location.
   */
  public String register(String url, String id, int x, int y, int w, int h) {
        
    Image picture = new Image(url, w, h, false, true);
    Canvas newCanvas = new Canvas((double) w + x, (double) h + y);

    GraphicsContext gc = newCanvas.getGraphicsContext2D();
    gc.drawImage(picture, x, y);

    if (images.putIfAbsent(id, newCanvas) == null) {
      return id;
    } else {
      return null;
    }
  }

  /**
   * Draw the image with the provided ID.
   */
  public boolean draw(String id) {
    if (images.containsKey(id) && !pane.getChildren().contains(images.get(id))) {
      pane.getChildren().add(images.get(id));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Undraws the image with the provided ID.
   */
  public boolean undraw(String id) {
    if (images.containsKey(id) && pane.getChildren().contains(images.get(id))) {
      pane.getChildren().remove(images.get(id));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Deregister the image with the provided ID.
   */
  public boolean deregister(String id) {
    if (images.containsKey(id)) {
      pane.getChildren().remove(images.get(id));
      images.remove(id);
      return true;
    } else {
      return false;
    }
  }
}
