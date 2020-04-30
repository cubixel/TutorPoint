import static services.ServerTools.sendFileService;

// import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import model.requests.PresentationChangeSlideRequest;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;

public class PresentationHandler extends Thread {
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
   * @param session the session joined.
   */
  public PresentationHandler(Session session) {
    setDaemon(true);
    setName("PresentationHandler");
    // this.dis = parent.getDataInputStream();
    slideNum = -1;
    this.session = session;
    
    // Make folder for uploads
    targetDirectory = "server/src/main/resources/uploaded/presentations/" + session.getSessionID() 
        + "/";
    File tempFile = new File(targetDirectory);
    tempFile.mkdirs();


    setXml("server/src/main/resources/presentations/DefaultPresentation.xml");
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
          //use session to change student's presentation
          session.getSessionUsers().forEach((id, handler) -> {
            log.info("Sending Slide Update to id " + id);
            
            sendXmlToClientListener(handler);
          });
        } else if (requestAction.equals("changeSlide")) {
          //this is necessary
          log.info("setting slide to: " + requestSlideNum);
          slideNum = requestSlideNum;
          //use session to change student's slides
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


  /**
   * Send the XML file to the client.
   */
  private boolean sendXml(DataOutputStream dos) {
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

  protected void setXml(String xmlUrl) {
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
   
  /**
   * Sends the XML of the PresentationHandler to the
   * client via their associated handler.
   * @param handler the ClientHandler associated with the target client
   */
  public void sendXmlToClientListener(ClientHandler handler) {
    //warn ListenerThread, then send xml
    handler.getNotifier().sendString("SendingPresentation");
    sendXml(handler.getNotifier().getDataOutputStream());
    handler.getNotifier().sendString(String.valueOf(slideNum));
  }
}