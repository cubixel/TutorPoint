package application.controller.presentation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */

public class TimingManager extends Thread {
  private static final Logger log = LoggerFactory.getLogger("TimingManager");
  private volatile long slideStartTime;
  private volatile long currentTime;
  private long timeElapsed;
  private long slideDuration;
  private int slideNumber = 0;
  private volatile int newSlideNumber = 0;
  private TimingNode tempNode;
  private boolean running = true;
  private ArrayList<LinkedList<TimingNode>> startTimesList = 
      new ArrayList<LinkedList<TimingNode>>();
  private ArrayList<LinkedList<TimingNode>> endTimesList = new ArrayList<LinkedList<TimingNode>>();
  private LinkedList<TimingNode> startTimes = new LinkedList<TimingNode>();
  private LinkedList<TimingNode> endTimes = new LinkedList<TimingNode>();
  private LinkedList<TimingNode> displayedNodes = new LinkedList<TimingNode>();
  private PresentationObject presentation;
  private TextHandler textHandler;
  private ImageHandler imageHandler;
  private VideoHandler videoHandler;
  private GraphicsHandler graphicsHandler;
  private AudioHandler audioHandler;

  /**
   * METHOD DESCRIPTION.
   */
  public TimingManager(PresentationObject presentation, StackPane pane) {
    setDaemon(true);
    setName("TimingManagerThread");

    this.presentation = presentation;

    //initialise element handlers
    this.textHandler = new TextHandler(pane, presentation.getDfFont(),
        presentation.getDfFontSize(), presentation.getDfFontColor());
    this.imageHandler = new ImageHandler(pane);
    this.videoHandler = new VideoHandler(pane);
    this.graphicsHandler = new GraphicsHandler(pane);
    this.audioHandler = new AudioHandler();
    List<PresentationSlide> slidesList = presentation.getSlidesList();
    PresentationSlide slide;
    List<Node> elements;
    Node element;
    String elementName;
    NamedNodeMap attributes;
    String tempId;
    String lineColor = presentation.getDfLineColor();
    String fillColor = presentation.getDfLineColor();
    NamedNodeMap shading;

    //load all slides into timing nodes
    for (int slideId = 0; slideId < slidesList.size(); slideId++) {
      slide = presentation.getSlidesList().get(slideId);
      startTimesList.add(new LinkedList<TimingNode>());
      endTimesList.add(new LinkedList<TimingNode>());

      elements = slide.getElementList();
      log.info("Making Slide " + slideId + " with " + elements.size() + " elements.");
      for (int elementId = 0; elementId < elements.size(); elementId++) {
        element = elements.get(elementId);
        elementName = element.getNodeName();
        attributes = element.getAttributes();
        tempId = slideId + ":" + elementId;
        switch (elementName) {
          case "text":
            textHandler.registerText(element, tempId);
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue(), 
                attributes.getNamedItem("endtime").getNodeValue());
            log.info("Text element made at ID " + tempId);
            break; 
          case "line":
            lineColor = presentation.getDfLineColor();
            try {
              lineColor = attributes.getNamedItem("linecolor").getNodeValue();
            } catch (NullPointerException e) {
              log.info("No linecolor found, using default");
            }
            graphicsHandler.registerLine(
                Float.parseFloat(attributes.getNamedItem("xstart").getNodeValue()) / 100,
                Float.parseFloat(attributes.getNamedItem("xend").getNodeValue()) / 100,
                Float.parseFloat(attributes.getNamedItem("ystart").getNodeValue()) / 100,
                Float.parseFloat(attributes.getNamedItem("yend").getNodeValue()) / 100, 
                lineColor, tempId);
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue(), 
                attributes.getNamedItem("endtime").getNodeValue());
            log.info("Line element made at ID " + tempId);
            break; 
          case "shape":
            if (element.getChildNodes().getLength() == 1) {
              shading = element.getChildNodes().item(0).getAttributes();
              if (attributes.getNamedItem("type").getTextContent().equals("oval")) {
                graphicsHandler.registerOval(
                    Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("width").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("height").getTextContent()) / 100,
                    tempId, 
                    Float.parseFloat(shading.getNamedItem("x1").getTextContent()) / 100,
                    Float.parseFloat(shading.getNamedItem("y1").getTextContent()) / 100,
                    shading.getNamedItem("color1").getTextContent(),
                    Float.parseFloat(shading.getNamedItem("x2").getTextContent()) / 100,
                    Float.parseFloat(shading.getNamedItem("y2").getTextContent()) / 100,
                    shading.getNamedItem("color2").getTextContent(), 
                    parseBoolean(shading.getNamedItem("cyclic").getTextContent())
                );
              } else {
                graphicsHandler.registerRectangle(
                    Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("width").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("height").getTextContent()) / 100,
                    tempId, 
                    Float.parseFloat(shading.getNamedItem("x1").getTextContent()) / 100,
                    Float.parseFloat(shading.getNamedItem("y1").getTextContent()) / 100,
                    shading.getNamedItem("color1").getTextContent(),
                    Float.parseFloat(shading.getNamedItem("x2").getTextContent()) / 100,
                    Float.parseFloat(shading.getNamedItem("y2").getTextContent()) / 100,
                    shading.getNamedItem("color2").getTextContent(), 
                    parseBoolean(shading.getNamedItem("cyclic").getTextContent())
                );
              }
            } else {
              fillColor = presentation.getDfFillColor();
              try {
                fillColor = attributes.getNamedItem("fillcolor").getNodeValue();
              } catch (NullPointerException e) {
                log.info("No fillcolor found, using default");
              }
              if (attributes.getNamedItem("type").getTextContent().equals("oval")) {
                graphicsHandler.registerOval(
                    Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("width").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("height").getTextContent()) / 100,
                    fillColor, tempId
                );
              } else {
                graphicsHandler.registerRectangle(
                    Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("width").getTextContent()) / 100,
                    Float.parseFloat(attributes.getNamedItem("height").getTextContent()) / 100,
                    fillColor, tempId
                );
              }

            }
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue(), 
                attributes.getNamedItem("endtime").getNodeValue());
            log.info("Shape element made at ID " + tempId);
            break;
          case "audio":
            audioHandler.registerAudio(
                attributes.getNamedItem("urlname").getTextContent(),
                parseBoolean(attributes.getNamedItem("loop").getTextContent()),
                tempId
            );
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue());
            log.info("Audio element made at ID " + tempId);
            break; 
          case "image":
            imageHandler.registerImage(attributes.getNamedItem("urlname").getTextContent(), tempId, 
                Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()),
                Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()), 
                Float.parseFloat(attributes.getNamedItem("width").getTextContent()), 
                Float.parseFloat(attributes.getNamedItem("height").getTextContent()));
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue(), 
                attributes.getNamedItem("endtime").getNodeValue());
            log.info("Image element made at ID " + tempId);
            break; 
          case "video":
            videoHandler.registerVideo(attributes.getNamedItem("urlname").getTextContent(), tempId, 
                Float.parseFloat(attributes.getNamedItem("xstart").getTextContent()),
                Float.parseFloat(attributes.getNamedItem("ystart").getTextContent()), 
                parseBoolean(attributes.getNamedItem("loop").getTextContent()));
            addElement(elementName, slideId, elementId, 
                attributes.getNamedItem("starttime").getNodeValue());
            log.info("Video element made at ID " + tempId);
            break; 
          default:
            break;
        }
      }
    } //end slides loop
    setSlide(0);
  }

  @Override
  public void run() {
    log.info("Starting...");
    Boolean moreToRemove = false;
    log.info("Start times detected: " + startTimes.size());
    log.info("End times detected: " + endTimes.size());
    while (running) {
      currentTime = System.currentTimeMillis();
      //if more than 1ms has passed
      if (currentTime - slideStartTime != timeElapsed) {
        //recalculate time since slide start
        timeElapsed = currentTime - slideStartTime;
        moreToRemove = false;
        do {
          if (!startTimes.isEmpty()) {
            //if the next thing to be started is due
            if (timeElapsed >= startTimes.getFirst().getTime()) {
              tempNode = startTimes.removeFirst();
              startElement(tempNode);
              //add to list of displayed nodes
              displayedNodes.add(tempNode);
              moreToRemove = true;
            //stop trying to start things this cycle
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
            //if the next thing to be ended is due
            if (timeElapsed >= endTimes.getFirst().getTime()) {
              tempNode = endTimes.removeFirst();
              endElement(tempNode);
              boolean notRemoved = true;
              int tempIndex = 0;
              //remove thing from list of displayed nodes
              while (notRemoved && (tempIndex < displayedNodes.size())) {
                if (displayedNodes.get(tempIndex).getId() == tempNode.getId()) {
                  displayedNodes.remove(tempIndex);
                  notRemoved = false;
                } else {
                  tempIndex = tempIndex + 1;
                }
              }
              //stop trying to end tings this cycle
            } else {
              moreToRemove = false;
            }
          } else {
            moreToRemove = false;
          }
        } while (moreToRemove);
        
        //if it is time to end the current slide
        if (timeElapsed >= slideDuration && slideDuration != -1) {
          log.info("Ended slide " + slideNumber + " at " + timeElapsed + " intended " 
              + slideDuration);
          //request slide change
          newSlideNumber = slideNumber + 1;
        }

        //if slide change has been requested
        if (slideNumber != newSlideNumber) {
          //change slide
          setSlide(newSlideNumber);
        }
      }
    }
  }

  private synchronized void setSlide(int number) {
    if (number < 0) {
      return;
    }
    timeElapsed = 0;

    log.info("Changing Slide to: " + number);
    this.slideNumber = number % presentation.getTotalSlides();
    this.newSlideNumber = slideNumber;
    clearSlide();
    startTimes = new LinkedList<>(startTimesList.get(this.slideNumber));
    endTimes = new LinkedList<>(endTimesList.get(this.slideNumber));
    log.info("Adding Slide Duration");
    PresentationSlide slide = presentation.getSlidesList().get(this.slideNumber);
    slideDuration = Long.valueOf(slide.getDuration());
    log.info("Added slide duration of " + slideDuration);
    slideStartTime = System.currentTimeMillis();
    
    log.info("Finished Changing Slide");
  }

  /**
   * Tells the TimingManager to change slide on command.
   * @param number the slide number to change to
   */
  public void changeSlideTo(int number) {
    this.newSlideNumber = number;
  }


  private void clearSlide() {
    //remove all currently displayed elements
    displayedNodes.forEach(node -> {
      endElement(node);
      log.warn("Node ID " + node.getId() + " was implicitly removed on slide change;" 
          + " ignore intended removal time");
    });
    displayedNodes.clear();
  }

  private void addElement(String name, int slideId, int elementId, String startTime, 
      String endTime) {
    int startIndex = 0;
    int endIndex = 0;
    Long startLong = Long.parseLong(startTime);
    Long endLong = Long.parseLong(endTime);
    boolean found = false;
    String id = slideId + ":" + elementId; 
    LinkedList<TimingNode> starts = startTimesList.get(slideId);
    LinkedList<TimingNode> ends = endTimesList.get(slideId);

    //insert each element into the relevant startTimesList entry, maintaining order
    while (startIndex < starts.size() && !found) {
      if (starts.get(startIndex).getTime() > startLong) {
        starts.add(startIndex, new TimingNode(id, startLong, name));
        found = true;
      }
      startIndex = startIndex + 1;
    }
    //if its position was not found, add to the end
    if (!found) {
      starts.add(new TimingNode(id, startLong, name));
    }

    //insert each element into the relevant endTimesList entry, maintaining order
    if (!endTime.equals("-1")) {
      found = false;
      while (endIndex < ends.size() && !found) {
        if (ends.get(endIndex).getTime() > endLong) {
          ends.add(endIndex, new TimingNode(id, endLong, name));
          found = true;
        }
        endIndex = endIndex + 1;
      }
      //if its position was not found, add to the end
      if (!found) {
        ends.add(new TimingNode(id, endLong, name));
      }
    }
  }

  private void addElement(String name, int slideId, int elementId, String startTime) {
    addElement(name, slideId, elementId, startTime, "-1");
  }

  private synchronized void startElement(TimingNode element) {
    String elementName = element.getType();
    switch (elementName) {
      case "text":
        Platform.runLater(() -> {
          textHandler.drawText(element.getId());
        });
        break; 
      case "line":
        Platform.runLater(() -> {
          graphicsHandler.drawGraphic(element.getId());
        });
        break; 
      case "shape":
        Platform.runLater(() -> {
          graphicsHandler.drawGraphic(element.getId());
        });
        break;
      case "audio":
        Platform.runLater(() -> {
          audioHandler.startAudio(element.getId());
        });
        break; 
      case "image":
        Platform.runLater(() -> {
          imageHandler.drawImage(element.getId());
        });
        break; 
      case "video":
        Platform.runLater(() -> {
          videoHandler.startVideo(element.getId());
        });
        break; 
      default:
        break;
    }
    log.info("Started " + element.getType() + " element " + element.getId() 
        + " @ time: " + timeElapsed + " Intended: " + element.getTime());
  }

  private synchronized void endElement(TimingNode element) {
    String elementName = element.getType();
    switch (elementName) {
      case "text":
        Platform.runLater(() -> {
          textHandler.undrawText(element.getId());
        });
        break; 
      case "line":
        Platform.runLater(() -> {
          graphicsHandler.undrawGraphic(element.getId());
        });
        break; 
      case "shape":
        Platform.runLater(() -> {
          graphicsHandler.undrawGraphic(element.getId());
        });
        break;
      case "audio":
        Platform.runLater(() -> {
          audioHandler.stopAudio(element.getId());
        });
        break;
      case "image":
        Platform.runLater(() -> {
          imageHandler.undrawImage(element.getId());
        });
        break; 
      case "video":
        Platform.runLater(() -> {
          videoHandler.stopVideo(element.getId());
        });
        break; 
      default:
        break;
    }
    log.info("Ended " + element.getType() + " element " + element.getId() 
        + " @ time: " + timeElapsed + " Intended: " + element.getTime());
  }

  /**
   * parses XML boolean values to java boolean values.
   *  "true"  -> true | 
   *"false" -> false | 
   *"1"     -> true | 
   *"0"     -> false | 
   *others  -> null | 
   *null    -> null | 
   * 
   * @param string the string boolean to be parsed
   * @return the java boolean
   */
  public static Boolean parseBoolean(String string) {
    if (string != null) {
      switch (string) {
        case "true" :
          return true;
        case "false" :
          return false;
        case "1" :
          return true;
        case "0" :
          return false;
        default:
          return null;
      }
    } else {
      return null;
    }
  }

  public PresentationObject getPresentation() {
    return presentation;
  }

  public void stopManager() {
    clearSlide();
    this.running = false;
  }

  public int getSlideNumber() {
    return slideNumber;
  }
}