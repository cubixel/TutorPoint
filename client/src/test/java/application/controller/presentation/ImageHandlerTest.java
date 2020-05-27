package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * // TODO Test Description.
 *
 * @author Daniel Bishop
 * @author Eric Walker
 */
public class ImageHandlerTest {

  protected ImageHandler imageHandler;

  /**
   * Tests adding a single image to the map.
   */
  public void registerOneImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:0",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    assertTrue(imageHandler.getImagesMap().size() == 1);
  }

  /**
   * Tests registering two images to the map.
   */
  public void registerTwoImages() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:0",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    assertTrue(imageHandler.getImagesMap().size() == 2);
  }

  /**
   * Tests registering two images to the map with identical IDs.
   */
  public void registerTwoImagesSameId() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:0",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:0",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    assertTrue(imageHandler.getImagesMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Image.
   */
  public void deregisterExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.deregisterImage("0:1");
    assertTrue(imageHandler.getImagesMap().size() == 0);
  }

  /**
   * Tests deregistering a non-extant Image.
   */
  public void deregisterNonExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:2",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.deregisterImage("0:1");
    assertTrue(imageHandler.getImagesMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Image twice.
   */
  public void deregisterExtantImageTwice() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:0",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.deregisterImage("0:1");
    imageHandler.deregisterImage("0:1");
    assertTrue(imageHandler.getImagesMap().size() == 1);
  }

  /**
   * Tests drawing an extant image.
   */
  public void drawExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(imageHandler.drawImage("0:1"));
    
  }

  /**
   * Tests drawing a non-extant image.
   */
  public void drawNonExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertFalse(imageHandler.drawImage("0:2"));
    
  }
  
  /**
   * Tests drawing an extant image twice.
   */
  public void drawExtantImageTwice() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:2",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(imageHandler.drawImage("0:1"));
    assertFalse(imageHandler.drawImage("0:1"));
    
  }

  /**
   * Tests undrawing an extant image.
   */
  public void unDrawExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    imageHandler.drawImage("0:1");
    assertTrue(imageHandler.undrawImage("0:1"));
    
  }

  /**
   * Tests undrawing a non-extant image.
   */
  public void unDrawNonExtantImage() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    imageHandler.drawImage("0:1");
    assertFalse(imageHandler.undrawImage("0:2"));
    
  }
  
  /**
   * Tests undrawing an extant image twice.
   */
  public void unDrawExtantImageTwice() {
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:1",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    imageHandler.registerImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "0:2",
        (float)0.5, (float)0.5, (float)0.5, (float)0.5);
    //can't mock the pane, so we just gotta trust that the return is correct
    imageHandler.drawImage("0:1");
    imageHandler.drawImage("0:2");
    assertTrue(imageHandler.undrawImage("0:1"));
    assertFalse(imageHandler.undrawImage("0:1"));
    
  }

}
