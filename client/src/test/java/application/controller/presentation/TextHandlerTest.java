package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import javafx.scene.layout.StackPane;
import org.mockito.Spy;
import org.w3c.dom.Node;

/**
 * // TODO Test Description.
 *
 * @author Eric Walker
 */
public class TextHandlerTest {

  protected TextHandler textHandler;
  protected Node textNode;

  /**
   * Tests adding a single text to the map.
   */
  public void registerOneText() {
    textHandler.registerText(textNode, "0:0");
    assertTrue(textHandler.getTextsMap().size() == 1);
  }

  /**
   * Tests registering two texts to the map.
   */
  public void registerTwoTexts() {
    textHandler.registerText(textNode, "0:0");
    textHandler.registerText(textNode, "0:1");
    assertTrue(textHandler.getTextsMap().size() == 2);
  }

  /**
   * Tests registering two texts to the map with identical IDs.
   */
  public void registerTwoTextsSameId() {
    textHandler.registerText(textNode, "0:0");
    textHandler.registerText(textNode, "0:0");
    assertTrue(textHandler.getTextsMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Text.
   */
  public void deregisterExtantText() {
    textHandler.registerText(textNode, "0:1");
    textHandler.deregisterText("0:1");
    assertTrue(textHandler.getTextsMap().size() == 0);
  }

  /**
   * Tests deregistering a non-extant Text.
   */
  public void deregisterNonExtantText() {
    textHandler.registerText(textNode, "0:2");
    textHandler.deregisterText("0:1");
    assertTrue(textHandler.getTextsMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Text twice.
   */
  public void deregisterExtantTextTwice() {
    textHandler.registerText(textNode, "0:1");
    textHandler.registerText(textNode, "0:0");
    textHandler.deregisterText("0:1");
    textHandler.deregisterText("0:1");
    assertTrue(textHandler.getTextsMap().size() == 1);
  }

  /**
   * Tests drawing an extant text.
   */
  public void drawExtantText() {
    textHandler.registerText(textNode, "0:1");
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(textHandler.drawText("0:1"));
    
  }

  /**
   * Tests drawing a non-extant text.
   */
  public void drawNonExtantText() {
    textHandler.registerText(textNode, "0:1");
    //can't mock the pane, so we just gotta trust that the return is correct
    assertFalse(textHandler.drawText("0:2"));
    
  }
  
  /**
   * Tests drawing an extant text twice.
   */
  public void drawExtantTextTwice() {
    textHandler.registerText(textNode, "0:1");
    textHandler.registerText(textNode, "0:2");
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(textHandler.drawText("0:1"));
    assertFalse(textHandler.drawText("0:1"));
    
  }

  /**
   * Tests undrawing an extant text.
   */
  public void unDrawExtantText() {
    textHandler.registerText(textNode, "0:1");
    //can't mock the pane, so we just gotta trust that the return is correct
    textHandler.drawText("0:1");
    assertTrue(textHandler.undrawText("0:1"));
    
  }

  /**
   * Tests undrawing a non-extant text.
   */
  public void unDrawNonExtantText() {
    textHandler.registerText(textNode, "0:1");
    //can't mock the pane, so we just gotta trust that the return is correct
    textHandler.drawText("0:1");
    assertFalse(textHandler.undrawText("0:2"));
    
  }
  
  /**
   * Tests undrawing an extant text twice.
   */
  public void unDrawExtantTextTwice() {
    textHandler.registerText(textNode, "0:1");
    textHandler.registerText(textNode, "0:2");
    //can't mock the pane, so we just gotta trust that the return is correct
    textHandler.drawText("0:1");
    textHandler.drawText("0:2");
    assertTrue(textHandler.undrawText("0:1"));
    assertFalse(textHandler.undrawText("0:1"));
    
  }

}
