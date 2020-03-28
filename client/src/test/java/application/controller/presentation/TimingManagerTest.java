package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.services.XmlHandler;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TimingManagerTest {

  private Boolean checkPresentation(PresentationObject presentation, Boolean valid, String author,
      String dateModified, String version, int totalSlides, String comment, 
      String dfBackgroundColor, String dfFont, int dfFontSize, String dfFontColor, 
      String dfLineColor, String dfFillColor, int dfSlideWidth, int dfSlideHeight) {
    if (checkPresentationDocumentInfo(presentation, author,
        dateModified, version, totalSlides, comment) 
        && checkPresentationDefaults(presentation, 
        dfBackgroundColor, dfFont, dfFontSize, dfFontColor, 
        dfLineColor, dfFillColor, dfSlideWidth, dfSlideHeight)
        && presentation.getValid() == valid) {
      return true;
    }
    
    return false;
  }

  private Boolean checkPresentationDocumentInfo(PresentationObject presentation, String author,
      String dateModified, String version, int totalSlides, String comment) {
    if (presentation.getAuthor().equals(author) 
        && presentation.getDateModified().equals(dateModified) 
        && presentation.getVersion().equals(version)
        && presentation.getTotalSlides() == totalSlides 
        && presentation.getComment().equals(comment)) {
      return true;
    }
    return false;
  }

  private Boolean checkPresentationDefaults(PresentationObject presentation, 
      String dfBackgroundColor, String dfFont, int dfFontSize, String dfFontColor, 
      String dfLineColor, String dfFillColor, int dfSlideWidth, int dfSlideHeight) {
    if (presentation.getDfBackgroundColor().equals(dfBackgroundColor)
        && presentation.getDfFont().equals(dfFont)
        && presentation.getDfFontSize() == dfFontSize
        && presentation.getDfFontColor().equals(dfFontColor)
        && presentation.getDfLineCOlor().equals(dfLineColor)
        && presentation.getDfFillColor().equals(dfFillColor)
        && presentation.getDfSlideWidth() == dfSlideWidth
        && presentation.getDfSlideHeight() == dfSlideHeight) {
      return true;
    }
    return false;
  }

  private void printPresentationFields(PresentationObject presentation) {
    System.out.println("valid: " + presentation.getValid());

    System.out.println("author: " + presentation.getAuthor());
    System.out.println("datemodified: " + presentation.getDateModified());
    System.out.println("version: " + presentation.getVersion());
    System.out.println("totalslides: " + presentation.getTotalSlides());
    System.out.println("comment: " + presentation.getComment());

    System.out.println("backgroundcolor: " + presentation.getDfBackgroundColor());
    System.out.println("font: " + presentation.getDfFont());
    System.out.println("fontsize: " + presentation.getDfFontSize());
    System.out.println("fontcolor: " + presentation.getDfFontColor());
    System.out.println("linecolor: " + presentation.getDfLineCOlor());
    System.out.println("fillcolor: " + presentation.getDfFillColor());
    System.out.println("slidewidth: " + presentation.getDfSlideWidth());
    System.out.println("slideheight: " + presentation.getDfSlideHeight());
  }

  private void mySleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      //yeet
    }
  }

  @Test
  public void testSlideTiming() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationJustSlides.xml");
    handler.parseToDom();
    PresentationObject presentation = new PresentationObject(handler.getDoc());
    TimingManager timingManager = new TimingManager(presentation);
    timingManager.start();
    mySleep(1000);
    
    
    //printPresentationFields(presentation);
    //assertTrue(checkPresentation(presentation, true, "test1", "test2", "test3", 2, "test4", 
    //    "#FFFFFF", "Arial", 11, "#FFFFFF", "#FFFFFF", "#FFFFFF", 12, 13));
  }

  @Test
  public void randomTest() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationBasic.xml");
    handler.parseToDom();
    PresentationObject presentation = new PresentationObject(handler.getDoc());
    TimingManager timingManager = new TimingManager(presentation);
    timingManager.start();
    mySleep(15000);
  }
}