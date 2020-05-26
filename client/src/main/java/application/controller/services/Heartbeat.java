package application.controller.services;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used to repeatedly ping the server from
 * the client to let the server know this client is
 * still connected. It pings the server every two
 * seconds.
 *
 * @author Daniel Bishop
 * @version 1.0
 * @see MainConnection
 */
public class Heartbeat extends Thread {

  private final MainConnection connection;
  private boolean connected;

  private static final Logger log = LoggerFactory.getLogger("Heartbeat");

  /**
   * Constructor for the Heartbeat class. The Heartbeat
   * thread is a Daemon thread.
   */
  public Heartbeat(MainConnection connection) {
    setDaemon(true);
    setName("Heartbeat");
    this.connection = connection;
    this.connected = true;
  }

  /**
   * Stops the heartbeat for a given client's connection.
   */
  public void stopHeartbeat() {
    this.connected = false;
  }

  /**
   * Starts the heartbeat for a given client's connection.
   */
  @Override
  public void run() {
    while (connected) {
      try {
        connection.sendString("Heartbeat");
      } catch (IOException e) {
        connected = false;
        log.error("Could not send data to server ", e);
      }

      try {
        sleep(2000);
      } catch (InterruptedException e) {
        log.error("Thread could not sleep ", e);
      }
    }
  }
}