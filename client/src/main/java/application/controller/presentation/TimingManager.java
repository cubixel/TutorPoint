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
  private TimingNode tempNode;
  private LinkedList<TimingNode> startTimes = new LinkedList<TimingNode>();
  private LinkedList<TimingNode> endTimes = new LinkedList<TimingNode>();
  private LinkedList<TimingNode> displayedNodes = new LinkedList<TimingNode>();
  private PresentationObject presentation;

  public TimingManager(PresentationObject presentation) {
    this.presentation = presentation;
    setDaemon(true);
  }

  @Override
  public void run() {
    System.out.println("Starting...");
    Boolean moreToRemove = false;
    setSlide(0);
    System.out.println("Start times detected: " + startTimes.size());
    System.out.println("End times detected: " + endTimes.size());
    while (true) {
      currentTime = System.currentTimeMillis();
      if (currentTime - slideStartTime != timeElapsed) {
        timeElapsed = currentTime - slideStartTime;
        moreToRemove = false;
        do {
          if (!startTimes.isEmpty()) {
            if (timeElapsed >= startTimes.getFirst().getTime()) {
              tempNode = startTimes.removeFirst();
              startElement(tempNode);
              displayedNodes.add(tempNode);
              moreToRemove = true;
            } else {
              moreToRemove = false;
            }
          } else {
            moreToRemove = false;
          }

        } while (moreToRemove);

        moreToRemove = false;
        do {
          if (!endTimes.isEmpty()) {
            if (timeElapsed >= endTimes.getFirst().getTime()) {
              tempNode = endTimes.removeFirst();
              endElement(tempNode);
              displayedNodes.remove(tempNode);
            } else {
              moreToRemove = false;
            }
          } else {
            moreToRemove = false;
          }
        } while (moreToRemove);

        if (timeElapsed >= slideDuration && slideDuration != -1) {
          System.out.println("ended slide " + slideNumber + " at " + timeElapsed + " intended " 
              + slideDuration);
          setSlide(slideNumber + 1);
        }
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
    System.out.println("Making Slide " + slideNumber + " with " + elements.size() + " elements.");
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
          System.out.println("Text element made at ID " + tempId);
          break; 
        case "line":
          //lineHandler.register(element, dfLineColor);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          System.out.println("Line element made at ID " + tempId);
          break; 
        case "shape":
          //shapeHandler.register(element, dfFillColor);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          System.out.println("Shape element made at ID " + tempId);
          break;
        case "audio":
          //audioHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue());
          System.out.println("Audio element made at ID " + tempId);
          break; 
        case "image":
          //shapeHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue(), 
              attributes.getNamedItem("endtime").getNodeValue());
          System.out.println("Image element made at ID " + tempId);
          break; 
        case "video":
          //audioHandler.register(element);
          tempId = i;
          addElement(elementName, tempId, attributes.getNamedItem("starttime").getNodeValue());
          System.out.println("Video element made at ID " + tempId);
          break; 
        default:
          break;
      }
    }

    System.out.println("Adding Slide Duration");
    addSlideTimer(slide.getDuration());
    System.out.println("Added slide duration of " + slideDuration);
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
      startIndex = startIndex + 1;
    }
    if (!found) {
      startTimes.add(new TimingNode(id, startLong, name));
    }

    if (!endTime.equals("-1")) {
      found = false;
      while (endIndex < endTimes.size() && !found) {
        if (endTimes.get(endIndex).getTime() > endLong) {
          endTimes.add(endIndex, new TimingNode(id, endLong, name));
          found = true;
        }
        endIndex = endIndex + 1;
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
      startIndex = startIndex + 1;
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
    displayedNodes.forEach(node -> {
      endElement(node);
      System.out.println("Node ID " + node.getId() + "was implicitly removed on slide change");
    });
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

  public long getSlideStartTime() {
    return slideStartTime;
  }

  public void setSlideStartTime(long slideStartTime) {
    this.slideStartTime = slideStartTime;
  }

  public long getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  public long getTimeElapsed() {
    return timeElapsed;
  }

  public void setTimeElapsed(long timeElapsed) {
    this.timeElapsed = timeElapsed;
  }

  public long getSlideDuration() {
    return slideDuration;
  }

  public void setSlideDuration(long slideDuration) {
    this.slideDuration = slideDuration;
  }

  public int getSlideNumber() {
    return slideNumber;
  }

  public void setSlideNumber(int slideNumber) {
    this.slideNumber = slideNumber;
  }

  public TimingNode getTempNode() {
    return tempNode;
  }

  public void setTempNode(TimingNode tempNode) {
    this.tempNode = tempNode;
  }

  public LinkedList<TimingNode> getStartTimes() {
    return startTimes;
  }

  public void setStartTimes(LinkedList<TimingNode> startTimes) {
    this.startTimes = startTimes;
  }

  public LinkedList<TimingNode> getEndTimes() {
    return endTimes;
  }

  public void setEndTimes(LinkedList<TimingNode> endTimes) {
    this.endTimes = endTimes;
  }

  public LinkedList<TimingNode> getDisplayedNodes() {
    return displayedNodes;
  }

  public void setDisplayedNodes(LinkedList<TimingNode> displayedNodes) {
    this.displayedNodes = displayedNodes;
  }

  public PresentationObject getPresentation() {
    return presentation;
  }

  public void setPresentation(PresentationObject presentation) {
    this.presentation = presentation;
  }
}
//TODO make stuff private