import static services.ServerTools.sendFileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationHandler extends Thread {

  private int token;
  private ServerSocket dataSock;
  private Socket presSock;
  private DataInputStream presIn;
  private DataOutputStream presOut;
  private DataOutputStream oldOut;

  private File currentXml = null;
  private static final Logger log = LoggerFactory.getLogger("PresentationHandler");

  /**
   * Class to handler mirroring an XML presentation between users.
   */
  public PresentationHandler(DataOutputStream oldOut, int token, ServerSocket dataSock) {
    setDaemon(true);
    setName("PresentationHandler-" + token);
    this.presSock = new Socket();
    this.oldOut = oldOut;
    this.token = token;
    this.dataSock = dataSock;
    setXml("server/src/main/resources/presentations/ValidPresentation.xml");
    log.info("Spawned PresentationHandler successfully");
  }

  /**
   * Run the requested action in the PresentationHandler Thread.
   * 
   * @param action The name of the action to perform.
   */
  public void run(String action) {
    if (action.equals("SendXml")) {
      try {
        sendXml();
      } catch (IOException e) {
        log.error("Failed to send presentation", e);
      }
    } else if (action.equals("Connect")) {
      try {
        acceptConnection();
      } catch (IOException e) {
        log.error("Failed to send port number", e);
      }
    }
  }

  /**
   * Send the XML file to the client.
   * 
   * @throws IOException Failed to send file
   */
  public void sendXml() throws IOException {
    log.info("Sending file...");
    sendFileService(presOut, currentXml);
  }

  public void setXml(String xmlUrl) {
    currentXml = new File(xmlUrl);
    log.info("Using file at: " + currentXml.getAbsolutePath());
  }

  public File getCurrentXml() {
    return currentXml;
  }

  /**
   * Accept an incoming connection to the dataSocket.
   * 
   * @throws IOException Error
   */
  public void acceptConnection() throws IOException {
    presSock = dataSock.accept();
    this.presIn = new DataInputStream(presSock.getInputStream());
    this.presOut = new DataOutputStream(presSock.getOutputStream());

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error("Failed to sleep");
    }
    presOut.writeUTF("Verify: " + token);
  }
}