package application.controller.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerThread extends Thread {

  private TextChatService textChatService;
  private String targetAddress;
  private int targetPort;
  private Socket newSock;
  private DataInputStream listenIn;
  private DataOutputStream listenOut;

  private static final Logger log = LoggerFactory.getLogger("Listener");

  /**
   * Thread to listen for updates from server.
   */
  public ListenerThread(String address, int port, int token) throws IOException {
    setDaemon(true);
    setName("ListenerThread");
    this.targetAddress = address;
    this.targetPort = port;

    /* try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      log.error("Interrupted by something? (Not meant to be...)");
    } */

    newSock = new Socket(targetAddress, targetPort);
    listenIn = new DataInputStream(newSock.getInputStream());
    listenOut = new DataOutputStream(newSock.getOutputStream());
    listenOut.writeInt(token);
    log.info("Successfully registered data connection with token " + listenIn.readInt());
  }

  public void setTextChatService(TextChatService service){
    this.textChatService = service;
  }

  @Override
  public void run() {
    String received = null;
    while (true) {
      try {

        while (listenIn.available() > 0) {
          received = listenIn.readUTF();
        }
        if (received != null) {
          try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            String action = jsonObject.get("Class").getAsString();
            log.info("Requested: " + action);

            // Code for different actions goes here
            // (use the 'if (action.equals("ActionName"))' setup from ClientHandler)

            // End action code
            
          } catch (JsonSyntaxException e) {
            log.error("Received String: " + received);
          }
          received = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Send a file using the client->server side of the second connection.
   * 
   * @param file The file to send
   * @throws IOException Communication Error occured
   */
  public void sendFile(File file) throws IOException {
    final Logger log = LoggerFactory.getLogger("SendFileLogger");

    byte[] byteArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    dis.readFully(byteArray, 0, byteArray.length);
    log.info("Sending filename '" + file.getName() + "' of size " + byteArray.length);
    listenOut.writeUTF(file.getName());
    listenOut.writeLong(byteArray.length);
    listenOut.write(byteArray, 0, byteArray.length);
    listenOut.flush();
    dis.close();
  }
}