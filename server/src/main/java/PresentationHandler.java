import static services.ServerTools.sendFileService;

// import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationHandler extends Thread {

  // private final DataInputStream dis;
  private final DataOutputStream dos;
  private File currentXml = null;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandler");
  private volatile String action = null;
  private volatile int slideNum = -1;
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
      if (action != null) {
        if (action.equals("sendXml")) {
          log.info("Sending Xml");
          sendXml();
        } else if (action.equals("uploadXml")) {
          log.info("Uploading Xml");
          uploadXml();
        } else if (action.equals("changeSlide")) {
          log.info("setting slide to: " + slideNum);
          //TODO use session to change student's slides
          session.getSessionUsers().forEach((id, handler) -> {
            //TODO actually send something using the notifier
            log.info("Sending Slide Update to id " + id);
            handler.getNotifier();
          });
        }
        action = null;
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
  public boolean sendXml() {
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

  public void setAction(String action) {
    this.action = action;
  }

  public void setSlideNum(int slideNum) {
    this.slideNum = slideNum;
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
}