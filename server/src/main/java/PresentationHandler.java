import static services.ServerTools.sendFileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationHandler extends Thread {

  private int token;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private File currentXml = null;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandler");

  /**
   * Class to handler mirroring an XML presentation between users.
   */
  public PresentationHandler(DataInputStream dis, DataOutputStream dos, int token) {
    setDaemon(true);
    setName("PresentationHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    setXml("server/src/main/resources/presentations/ValidPresentation.xml");
    log.info("Spawned PresentationHandler successfully");
  }

  /**
   * Run the requested action in the PresentationHandler Thread.
   * 
   * @param action The name of the action to perform.
   */
  public void run(String action) {
    if (action.equals("sendXml")) {
      sendXml();
    }
  }

  /**
   * Send the XML file to the client.
  */
  public void sendXml() throws IOException {
    try {
      log.info("Sending file...");
      sendFileService(dos, currentXml);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error("Failed to send xml file to client");
      e.printStackTrace();
      throw e;
    }
  }

  public void setXml(String xmlUrl) {
    currentXml = new File(xmlUrl);
    log.info("Using file at: " + currentXml.getAbsolutePath());
  }

  public File getCurrentXml() {
    return currentXml;
  }
}