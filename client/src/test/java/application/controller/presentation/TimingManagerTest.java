package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import application.controller.presentation.exceptions.PresentationCreationException;
import application.controller.presentation.exceptions.XmlLoadingException;
import java.util.LinkedList;
import javafx.scene.layout.StackPane;

public class TimingManagerTest {

  public StackPane stackPane;

  private void mySleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      //yeet
    }
  }

  private boolean compareLinkedLists(LinkedList<TimingNode> a, LinkedList<TimingNode> b) {
    //System.out.println(a.size() + " = " + b.size());
    if (a.size() != b.size()) {
      return false;
    }
    for (int i = 0; i < a.size(); i++) {
      /*System.out.println(a.get(i).getId() + " = " + b.get(i).getId());
      System.out.println(a.get(i).getTime() + " = " + b.get(i).getTime());
      System.out.println(a.get(i).getType() + " = " + b.get(i).getType());*/
      if (!(a.get(i).getId().equals(b.get(i).getId()))) {
        return false;
      }
      if (a.get(i).getTime() != b.get(i).getTime()) {
        return false;
      }
      if (!(a.get(i).getType().equals(b.get(i).getType()))) {
        return false;
      }
    }
    return true;
  }

  public void testSlideTiming() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
            "src/main/resources/application/media/XML/TimingManager/"
            + "TimingPresentationJustSlides.xml");
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      TimingManager timingManager = new TimingManager(presentation, new StackPane());
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

  public void testSlideTimingNeg1() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationSlideNeg1.xml");
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      TimingManager timingManager = new TimingManager(presentation, new StackPane());
      timingManager.start();
      assertTrue(timingManager.getSlideNumber() == 0);
      mySleep(10);
      assertTrue(timingManager.getSlideNumber() == 0);
      mySleep(50);
      assertTrue(timingManager.getSlideNumber() == 0);
      timingManager.changeSlideTo(1);
      mySleep(10);
      assertTrue(timingManager.getSlideNumber() == 1);
      mySleep(2000);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    } catch (PresentationCreationException e) {
      e.printStackTrace();
    }
  }

  public void testElementTiming() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationElementTest.xml");
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    }
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
    TimingManager timingManager = null;
    try {
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      timingManager = new TimingManager(presentation, stackPane);
    } catch (PresentationCreationException e) {
      e.printStackTrace();
    }
    
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

  //Must look in the logs to see if this one works
  public void testImplicitRemovalVisual() {
    XmlHandler handler = new XmlHandler();
    try {
      handler.makeXmlFromUrl(
          "src/main/resources/application/media/XML/TimingManager/"
          + "TimingPresentationElementTest.xml");
      PresentationObject presentation = new PresentationObject(handler.getDoc());
      TimingManager timingManager = new TimingManager(presentation, stackPane);
      timingManager.start();
      mySleep(15000);
    } catch (XmlLoadingException e) {
      e.printStackTrace();
    } catch (PresentationCreationException e) {
      e.printStackTrace();
    }
    
  }
}