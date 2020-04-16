package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.presentation.exceptions.PresentationCreationException;
import application.controller.presentation.exceptions.XmlLoadingException;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;

public class TimingManagerTest {

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
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/TimingManager/"
            + "TimingPresentationJustSlides.xml");
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      StackPane pane = new StackPane();
      TextHandler textHandler = new TextHandler(pane, "Arial", 12, "#FFFFFF");
      ImageHandler imageHandler = new ImageHandler(pane);
      VideoHandler videoHandler = new VideoHandler(pane);
      TimingManager timingManager = new TimingManager(presentation, new StackPane(),
          textHandler,
          imageHandler, videoHandler);
      timingManager.start();
      assertTrue(timingManager.getSlideNumber() == 0);
      mySleep(1000);
      assertTrue(timingManager.getSlideNumber() == 1);
      mySleep(1000);
      assertTrue(timingManager.getSlideNumber() == 0);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    } catch (PresentationCreationException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSlideTimingNeg1() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationSlideNeg1.xml");
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      StackPane pane = new StackPane();
      TextHandler textHandler = new TextHandler(pane, "Arial", 12, "#FFFFFF");
      ImageHandler imageHandler = new ImageHandler(pane);
      VideoHandler videoHandler = new VideoHandler(pane);
      TimingManager timingManager = new TimingManager(presentation, new StackPane(),
          textHandler,
          imageHandler, videoHandler);
      timingManager.start();
      assertTrue(timingManager.getSlideNumber() == 0);
      mySleep(10);
      assertTrue(timingManager.getSlideNumber() == 0);
      mySleep(50);
      assertTrue(timingManager.getSlideNumber() == 0);
      timingManager.setSlide(1);
      assertTrue(timingManager.getSlideNumber() == 1);
      mySleep(2000);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    } catch (PresentationCreationException e) {
      e.printStackTrace();
    }
  }

  //thsese tests used to work, but now rely on being an application.
  /*@Test
  public void testElementTiming() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationElementTest.xml");
    handler.parseToDom();
    LinkedList<TimingNode> myStartTimes = new LinkedList<TimingNode>();
    myStartTimes.add(new TimingNode("0:1", 15, "shape"));
    myStartTimes.add(new TimingNode("0:4", 15, "shape"));
    myStartTimes.add(new TimingNode("0:0", 500, "image"));
    myStartTimes.add(new TimingNode("0:3", 909, "image"));
    myStartTimes.add(new TimingNode("0:2", 2500, "audio"));
    
    LinkedList<TimingNode> myEndTimes = new LinkedList<TimingNode>();
    myEndTimes.add(new TimingNode("0:1", 1500, "shape"));
    myEndTimes.add(new TimingNode("0:4", 5000, "shape"));
    myEndTimes.add(new TimingNode("0:0", 11000, "image"));
    PresentationObject presentation = new PresentationObject(handler.getDoc());
    TimingManager timingManager = new TimingManager(presentation);
    
    timingManager.start();
    mySleep(5);
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(15);
    myStartTimes.removeFirst();
    myStartTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(500 - 15);
    myStartTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(909 - 500);
    myStartTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(1500 - 909);
    myEndTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(2500 - 1500);
    myStartTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));

    mySleep(5000 - 2500);
    myEndTimes.removeFirst();
    assertTrue(compareLinkedLists(myStartTimes, timingManager.getStartTimes())
        && compareLinkedLists(myEndTimes, timingManager.getEndTimes()));
    mySleep(6000);
  }

  @Test
  public void testImplicitRemovalVisual() {
    XmlHandler handler = new XmlHandler();
    handler.openFile(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationBasic.xml");
    handler.parseToDom();
    PresentationObject presentation = new PresentationObject(handler.getDoc());
    TimingManager timingManager = new TimingManager(presentation);
    timingManager.start();
    mySleep(15000);
  }*/
}