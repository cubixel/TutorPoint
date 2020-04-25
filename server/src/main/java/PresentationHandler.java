import static services.ServerTools.sendFileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationHandler extends Thread {

  //private int token;
  // private final DataInputStream dis;
  private final DataOutputStream dos;
  private File currentXml = null;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandler");
  private volatile String action = null;
  private ClientHandler parent = null;
  private boolean running = true;
  String targetDirectory;

  /**
   * Class to handler mirroring an XML presentation between users.
   */
  public PresentationHandler(DataInputStream dis, DataOutputStream dos, int token,
      ClientHandler parent) {
    setDaemon(true);
    setName("PresentationHandler-" + token);
    // this.dis = dis;
    this.dos = dos;
    // this.token = token;
    this.parent = parent;
    
    // Make folder for uploads
    targetDirectory = "server/src/main/resources/uploaded/presentations/" + token + "/";
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
      File newXml = parent.getNotifier().listenForFile(targetDirectory);
      currentXml = newXml;
    } catch (IOException e) {
      log.error("Failed to read file from client", e);
    }
  }

  /**
   * Send the XML file to the client.
   */
  public boolean  sendXml() {
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