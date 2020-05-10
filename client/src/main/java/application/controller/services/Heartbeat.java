package application.controller.services;

import java.io.IOException;

/**
 * CLASS DESCRIPTION.
 *
 *
 * @author Daniel Bishop
 * @version 1.0
 */
public class Heartbeat extends Thread {

  private MainConnection connection;
  private boolean connected;

  /**
   * CONSTRUCTOR DESCRIPTION.
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
        e.printStackTrace();
      }

      try {
        sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
