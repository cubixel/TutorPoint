package application.controller.services;

import application.model.PresentationRequest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListenerThread extends Thread {

  private Socket newSock;
  private MainConnection connection;
  private DataInputStream listenIn;
  private DataOutputStream listenOut;

  private static final Logger log = LoggerFactory.getLogger("Listener");

  /**
   * Thread to listen for updates from server.
   */
  public ListenerThread(MainConnection connection) {
    setDaemon(true);
    setName("ListenerThread");
    this.connection = connection;
  }

  @Override
  public void run() {

    try {
      connection.sendString(connection.packageClass(new PresentationRequest("Connect")));
    } catch (IOException e) {
      log.error("Failed to send connect request", e);
    }

    try {
      newSock = new Socket(connection.getTargetAddress(), connection.getTargetPort() + 1);
      listenIn = new DataInputStream(newSock.getInputStream());
      listenOut = new DataOutputStream(newSock.getOutputStream());
    } catch (IOException e) {
      log.error("Failed to connect to data ServerSocket");
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