package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.services.XmlHandler;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class PresentationObjectTest {

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

  @Test
  public void makePresentation() {
    XmlHandler handler = new XmlHandler();
    Document xmlDoc = handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/PresentationObject/"
          + "TestXMLBasic.xml"
    );
    PresentationObject presentation = new PresentationObject(xmlDoc);
    printPresentationFields(presentation);
    assertTrue(checkPresentation(presentation, true, "test1", "test2", "test3", 2, "test4", 
        "#FFFFFF", "Arial", 11, "#FFFFFF", "#FFFFFF", "#FFFFFF", 12, 13));
  }

  @Test
  public void discardBadPresentationDocumentInfos() {
    XmlHandler handler;
    for (int i = 0; i <= 19; i++) {
      handler = new XmlHandler();
      System.out.println("testing file: " + i);
      Document xmlDoc = handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/PresentationObject/BadDocumentInfo/"
          + "PresentationBadDocumentInfo" + i + ".xml"
      );
      PresentationObject presentation = new PresentationObject(xmlDoc);
      //printPresentationFields(presentation);
      assertTrue(presentation.getValid() == false);
    }
  }

  @Test
  public void discardBadPresentationDefaults() {
    XmlHandler handler;
    for (int i = 0; i <= 21; i++) {
      handler = new XmlHandler();
      System.out.println("testing file: " + i);
      Document xmlDoc = handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/PresentationObject/BadDefaults/"
          + "PresentationBadDefaults" + i + ".xml"
      );
      PresentationObject presentation = new PresentationObject(xmlDoc);
      //printPresentationFields(presentation);
      assertTrue(presentation.getValid() == false);
    }
  }

  @Test
  public void discardBadSlides() {
    XmlHandler handler = new XmlHandler();
    Document xmlDoc = handler.makeXmlFromUrl(
        "src/main/resources/application/media/XML/PresentationObject/BadSlides/"
        + "PresentationBadSlides.xml"
    );
    PresentationObject presentation = new PresentationObject(xmlDoc);
    System.out.println(presentation.getSlidesList().size());
    assertTrue(presentation.getValid() == true && presentation.getSlidesList().size() == 1);
  }

  @Test
  public void discardMismatchedSlideNums() {
    XmlHandler handler = new XmlHandler();
    Document xmlDoc = handler.makeXmlFromUrl(
        "src/main/resources/application/media/XML/PresentationObject/BadSlides/"
        + "PresentationMismatchedSlideNums.xml"
    );
    PresentationObject presentation = new PresentationObject(xmlDoc);
    assertTrue(presentation.getValid() == false);
  }

}