package application.controller.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerThread extends Thread {

  private String targetAddress;
  private int targetPort;
  private Socket newSock;
  private DataInputStream listenIn;
  private DataOutputStream listenOut;
  private int token;

  private static final Logger log = LoggerFactory.getLogger("Listener");

  /**
   * Thread to listen for updates from server.
   */
  public ListenerThread(String address, int port, int token) {
    setDaemon(true);
    setName("ListenerThread");
    this.targetAddress = address;
    this.targetPort = port;
    this.token = token;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      newSock = new Socket(targetAddress, targetPort);
      listenIn = new DataInputStream(newSock.getInputStream());
      listenOut = new DataOutputStream(newSock.getOutputStream());
    } catch (IOException e) {
      log.error("Failed to connect to data ServerSocket");
    }

    try {
      listenOut.writeInt(token);
    } catch (IOException e) {
      log.error("Failed to send token", e);
    }

    log.info("Am I working? I think I'm connected to: " + newSock.getPort());
    log.info("Do I think I'm closed? " + newSock.isClosed());

    while (true) {
      try {
        if (listenIn.available() > 0) {
          log.info("Recieved: " + listenIn.readUTF());
        }
      } catch (IOException e) {
        log.error("Failed to echo input", e);
      }
    }
  }
}