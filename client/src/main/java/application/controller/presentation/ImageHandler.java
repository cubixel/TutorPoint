package application.controller.presentation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module to draw image files onto a StackPane by adding extra canvas.
 * Also handles deleting images via an ID assigned when drawn.
 *
 * @author Daniel Bishop
 * @author Eric Walker
 */
public class ImageHandler {

  private static final Logger log = LoggerFactory.getLogger("ImageHandler");

  private StackPane pane;
  private Map<String, Canvas> images = new ConcurrentHashMap<>();

  /**
   * Creates an instance of ImageHandler that draws to a specified StackPane.
   * @param targetPane The StackPane to draw to.
   */
  public ImageHandler(StackPane targetPane) {
    this.pane = targetPane;
  }

  /**
   * Registers an image from a URL onto a canvas, using the provided ID, dimensions and location.
   * @param url A URL linking to the desired Image file.
   * @param id An ID assigned to the image element to allow retrieval.
   * @param floatX The X position of the image on the slide, measured in percentage of slide width. 
   * @param floatY The Y position of the image on the slide, measured in percentage of slide 
   *      height. 
   * @param floatW The width of the image on the slide, measured in percentage of slide width. 
   * @param floatH The height of the image on the slide, measured in percentage of slide height. 
   * @return true if image was successfully registered or false if not.
   */
  public String registerImage(String url, String id,
      float floatX, float floatY, float floatW, float floatH) {
    
    // Calculate pixel values for x, y, w and h
    int x = Math.toIntExact(Math.round((floatX / 100) * pane.getMaxWidth()));
    int y = Math.toIntExact(Math.round((floatY / 100) * pane.getMaxHeight()));
    int w = Math.toIntExact(Math.round((floatW / 100) * pane.getMaxWidth()));
    int h = Math.toIntExact(Math.round((floatH / 100) * pane.getMaxHeight()));

    //load image and create canvas
    Image picture = new Image(url, w, h, false, true);
    Canvas newCanvas = new Canvas((double) w + x, (double) h + y);

    //draw image to canvas
    GraphicsContext gc = newCanvas.getGraphicsContext2D();
    gc.drawImage(picture, x, y);

    //add to map
    if (images.putIfAbsent(id, newCanvas) == null) {
      return id;
    } else {
      return null;
    }
  }

  /**
   * Draw the image with the provided ID.
   * @param id The ID of the image to draw.
   * @return Boolean whether the image was successfully drawn or not.
   */
  public boolean drawImage(String id) {
    //if image id exists and is not already displayed
    if (images.containsKey(id) && !pane.getChildren().contains(images.get(id))) {
      //display image
      pane.getChildren().add(images.get(id));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Undraws the image with the provided ID.
   * @param id The ID of the image to undraw.
   * @return Boolean whether the image was successfully undrawn or not.
   */
  public boolean undrawImage(String id) {
    //if image id exists and is displayed
    if (images.containsKey(id) && pane.getChildren().contains(images.get(id))) {
      //hide image
      pane.getChildren().remove(images.get(id));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Deregister the image with the provided ID.
   * @param id The ID of the image to deregister.
   * @return Boolean whether the image was successfully deregistered or not.
   */
  public boolean deregisterImage(String id) {
    //if image id exists
    if (images.containsKey(id)) {
      //hide image and deregister
      pane.getChildren().remove(images.get(id));
      images.remove(id);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validate provided URL.
   * @param url The url to validate.
   * @return Boolean true if URL is valid, false otherwise.
   */
  public static boolean validateUrl(String url) {
    try {
      new Image(url);
      log.info("Image url is valid");
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * getter for testing purposes.
   */
  Map<String, Canvas> getImagesMap() {
    return images;
  }
}
