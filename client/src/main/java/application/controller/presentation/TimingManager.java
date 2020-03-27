package application.controller.presentation;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */

public class TimingManager extends Thread {
  private long slideStartTime;
  private long currentTime;
  private long timeElapsed;
  private long slideDuration;
  private int slideNumber;
  private LinkedList<TimingNode> startTimes = new LinkedList<TimingNode>();
  private LinkedList<TimingNode> endTimes = new LinkedList<TimingNode>();
  private PresentationObject presentation;

  public TimingManager(PresentationObject presentation) {
    this.presentation = presentation;
    setDaemon(true);
  }

  @Override
  public void run() {
    Boolean moreToRemove = false;
    setSlide(0);
    System.out.println("set slide successfully");
    System.out.println(startTimes.size());
    System.out.println(endTimes.size());
    while (true) {
      currentTime = System.currentTimeMillis();
      timeElapsed = currentTime - slideStartTime;
      moreToRemove = false;
      do {
        if (!startTimes.isEmpty()) {
          if (timeElapsed >= startTimes.getFirst().getTime()) {
            startElement(startTimes.removeFirst());
            moreToRemove = true;
          } else {
            moreToRemove = false;
          }
        }
      } while (moreToRemove);

      do {
        if (!endTimes.isEmpty()) {
          if (timeElapsed >= endTimes.getFirst().getTime()) {
            endElement(endTimes.removeFirst());
            moreToRemove = true;
          } else {
            moreToRemove = false;
          }
        }
      } while (moreToRemove);

      if (timeElapsed >= slideDuration && slideDuration != -1) {
        System.out.println("ended slide " + slideNumber + " at " + timeElapsed + " intended " 
            + slideDuration);
        setSlide(slideNumber + 1);
      }

    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public void setSlide(int number) {
    this.slideNumber = number % presentation.getTotalSlides();
    PresentationSlide slide = presentation.getSlidesList().get(slideNumber);
    List<Node> elements = slide.getElementList();
    Node element;
    String elementName;
    NamedNodeMap attributes;
    int tempId;
    clearSlide();
    for (int i = 0; i < elements.size(); i++) {
      element = elements.get(i);
      elementName = element.getNodeName();
      attributes = element.getAttributes();
      switch (elementName) {
        case "text":
          //textHandler.register(element, dfFont, dfFontSIze, dfFontColor);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          break; 
        case "line":
          //lineHandler.register(element, dfLineColor);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          break; 
        case "shape":
          //shapeHandler.register(element, dfFillColor);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          break;
        case "audio":
          //audioHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue());
          break; 
        case "image":
          //shapeHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          break; 
        case "video":
          //audioHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue());
          break; 
        default:
          break;
      }
    }

    addSlideTimer(slide.getDuration());
    //draw the slide with the right size and colours (colors, sorry) n shit
    //maybe do that before we tell the managers to do stuff
    slideStartTime = System.currentTimeMillis(); //TODO put at end
  }

  /**
   * METHOD DESCRIPTION.
   */
  public void addElement(String name, int id, String startTime, String endTime) {
    int startIndex = 0;
    int endIndex = 0;
    Long startLong = Long.parseLong(startTime);
    Long endLong = Long.parseLong(endTime);
    boolean found = false;
    while (startIndex < startTimes.size() && !found) {
      if (startTimes.get(startIndex).getTime() > startLong) {
        startTimes.add(startIndex, new TimingNode(id, startLong, name));
        found = true;
      }
    }
    if (!found) {
      startTimes.add(new TimingNode(id, startLong, name));
    }

    if (endTime != "-1") {
      found = false;
      while (endIndex < endTimes.size() && !found) {
        if (endTimes.get(endIndex).getTime() > endLong) {
          endTimes.add(endIndex, new TimingNode(id, endLong, name));
          found = true;
        }
      }
      if (!found) {
        endTimes.add(new TimingNode(id, endLong, name));
      }
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public void addElement(String name, int id, String startTime) {
    int startIndex = 0;
    Long startLong = Long.parseLong(startTime);
    boolean found = false;
    while (startIndex < startTimes.size() && !found) {
      if (startTimes.get(startIndex).getTime() > startLong) {
        startTimes.add(startIndex, new TimingNode(id, startLong, name));
        found = true;
      }
    }
    if (!found) {
      startTimes.add(new TimingNode(id, startLong, name));
    }

  }

  /**
   * METHOD DESCRIPTION.
   */
  public void addSlideTimer(int duration) {
    slideDuration = Long.valueOf(duration);
    slideStartTime = System.currentTimeMillis();
  }

  /**
   * METHOD DESCRIPTION.
   */
  public void clearSlide() {
    startTimes.clear();
    endTimes.clear();
    slideStartTime = -1;
    slideDuration = -1;
  }

  private void startElement(TimingNode element) {
    System.out.println("Started " + element.getType() + " element " + element.getId() 
        + " @ time: " + timeElapsed + " Intended: " + element.getTime());
  }

  private void endElement(TimingNode element) {
    System.out.println("Ended " + element.getType() + " element " + element.getId() 
        + " @ time: " + timeElapsed + " Intended: " + element.getTime());
  }
}
//TODO make stuff private