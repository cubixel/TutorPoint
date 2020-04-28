import static services.ServerTools.sendFileService;

// import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.requests.PresentationChangeSlideRequest;
import services.ClientNotifier;

public class PresentationHandler extends Thread {

  // private final DataInputStream dis;
  private final DataOutputStream dos;
  private File currentXml = null;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandler");
  private volatile String requestAction = null;
  private volatile int slideNum;
  private volatile int requestSlideNum;
  private Session session = null;
  private boolean running = true;
  String targetDirectory;

  /**
   * Class to handler mirroring an XML presentation between users.
   */
  public PresentationHandler(Session session, int sessionID) {
    setDaemon(true);
    setName("PresentationHandler");
    // this.dis = parent.getDataInputStream();
    slideNum = -1;
    this.dos = session.getThisHandler().getDataOutputStream();
    this.session = session;
    
    // Make folder for uploads
    targetDirectory = "server/src/main/resources/uploaded/presentations/" + sessionID + "/";
    File tempFile = new File(targetDirectory);
    tempFile.mkdirs();


    setXml("server/src/main/resources/presentations/ValidPresentation.xml");
    log.info("Spawned PresentationHandler successfully");
  }

  /**
   * Run the requested action in the PresentationHandler Thread. Set the action
   * with setAction before calling start().
   */
  @Override
  public void run() {
    while (running) {
      if (requestAction != null) {
        if (requestAction.equals("uploadXml")) {
          log.info("Uploading Xml");
          uploadXml();
          slideNum = 0;
          //TODO use session to change student's presentation
          session.getSessionUsers().forEach((id, handler) -> {
            log.info("Sending Slide Update to id " + id);
            //warn ListenerThread, then send xml
            handler.getNotifier().sendString("SendingPresentation");
            sendXml(handler.getNotifier().getDataOutputStream());
            handler.getNotifier().sendString(String.valueOf(slideNum));
          });
        } else if (requestAction.equals("changeSlide")) {
          //this is necessary
          log.info("setting slide to: " + requestSlideNum);
          slideNum = requestSlideNum;
          //TODO use session to change student's slides
          session.getSessionUsers().forEach((id, handler) -> {
            log.info("Sending Slide Update to id " + id);
            handler.getNotifier().sendClass(new PresentationChangeSlideRequest(slideNum));
          });
        }
        requestAction = null;
      } else {
        try {
          sleep(100);
        } catch (InterruptedException e) {
          log.error("Interrupted", e);
        }
      }
      
    }
  }

  /**
   * Read an incoming file from the client.
   */
  private void uploadXml() {
    
    try {
      File newXml = session.getThisHandler().getNotifier().listenForFile(targetDirectory);
      currentXml = newXml;
    } catch (IOException e) {
      log.error("Failed to read file from client", e);
    }
  }


  //TODO bundle this so it sends slidenum and warning message
  /**
   * Send the XML file to the client.
   */
  public boolean sendXml(DataOutputStream dos) {
    try {
      log.info("Sending file...");
      sendFileService(dos, currentXml);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error("Failed to send xml file to client");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void setXml(String xmlUrl) {
    currentXml = new File(xmlUrl);
    log.info("Using file at: " + currentXml.getAbsolutePath());
  }

  public File getCurrentXml() {
    return currentXml;
  }

  public void setRequestAction(String action) {
    this.requestAction = action;
  }

  public void setRequestSlideNum(int slideNum) {
    this.requestSlideNum = slideNum;
  }

  /**
   * Deletes the tempory folder created to hold uploaded presentations.
   */
  public void exit() {
    // Stop infinite loop
    this.running = false;

    // Perform and necessary cleanup
    try {
      FileUtils.deleteDirectory(new File(targetDirectory));
    } catch (IOException e) {
      log.error("Failed to delete uploads", e);
    }
  }

  public int getSlideNum() {
    log.info("slideNum in getSlideNum: " + slideNum);
    return slideNum;
  }
}