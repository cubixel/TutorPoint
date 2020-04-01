/*
 * MainConnection.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

package application.controller.services;

import application.controller.enums.AccountRegisterResult;
import application.model.Account;
import application.model.Subject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;

/**
 * CLASS DESCRIPTION.
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MainConnection {
  private Socket socket = null;
  private DataInputStream dis = null;
  private DataOutputStream dos = null;
  private Heartbeat heartbeat = null;
  private String serverReply;
  private Logger log;

  /**
   * Constructor that creates a socket of a specific
   * IP Address and Port Number. And sets up data input
   * and output streams on that socket.
   *
   * @param connectionAdr IP Address for Connection.
   * @param port Port Number.
   */
  public MainConnection(String connectionAdr, int port, Logger log) throws IOException {
    this.log = log;

    /* If the connection address is null then it will default to localhost. */
    if (connectionAdr == null) {
      socket = new Socket("localhost", port);
      log.info("Connecting to Address: LocalHost on Port: " + port);
    } else {
      socket = new Socket(connectionAdr, port);
      log.info("Connecting to Address: " + connectionAdr + " on Port: " + port);
    }

    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());

    heartbeat = new Heartbeat(this);
    heartbeat.start();
  }

  /**
   * This is a constructor just used for testing the MainConnection Class.
   * @param dis A DataInputStream for the MainConnection to receive data.
   * @param dos A DataOutputStream for the MainConnection to send data.
   */
  public MainConnection(DataInputStream dis, DataOutputStream dos, Heartbeat heartbeat, Logger log) {
    this.dis = dis;
    this.dos = dos;
    this.heartbeat = heartbeat;
    this.log = log;
    heartbeat.start();
  }

  /* Takes a String as an input and sends this to the ##### */
  public void sendString(String input) throws IOException {
    dos.writeUTF(input);
  }

  /* Getter method for the state of the socket. */
  public boolean isClosed() {
    return socket.isClosed();
  }


  /**
   * Listens for incoming data. Timeout of 3s after which a network failure error is returned.
   */
  public String listenForString() throws IOException {
    String incoming = null;
    long startTime = System.currentTimeMillis();
    Boolean recieved = false;

    do {
      while (dis.available() > 0 && !recieved) {
        incoming = dis.readUTF();
        recieved = true;
      }
      // This waits 10 seconds for a response so make sure it comes in quicker than that.
    } while ((incoming == null) && ((System.currentTimeMillis() - startTime) <= 10000));
    if (incoming == null) {
      return AccountRegisterResult.FAILED_BY_NETWORK.toString();
    } else {
      return incoming;
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public File listenForFile() throws IOException {

    // TODO handle the exceptions better as it just throws a generic IOException.
    int bytesRead;

    String fileName = dis.readUTF();
    long size = dis.readLong();
    OutputStream output =
        new FileOutputStream("src/main/resources/application/media/downloads/" + fileName);
    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    output.close();

    return new File("client/src/main/resources/application/media/downloads/" + fileName);
  }

  private JsonObject listenForJson() throws IOException {
    serverReply = this.listenForString();

    Gson gson = new Gson();
    try {
      return gson.fromJson(serverReply, JsonObject.class);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      System.out.println(serverReply);
      return null;
    }
  }


  /**]
   *    * Listens for a string on the dis and then
   *    * attempts to create a subject object from the
   *    * json string.
   * @return The Subject sent from the server.
   * @throws IOException No String on DIS.
   */
  public Subject listenForSubject() throws IOException {

    JsonObject jsonObject = listenForJson();
    Subject subject;
    try {
      String action = jsonObject.get("Class").getAsString();

      if (action.equals("Subject")) {
        subject = new Subject(jsonObject.get("id").getAsInt(), jsonObject.get("name").getAsString(),
            jsonObject.get("nameOfThumbnailFile").getAsString(),
            jsonObject.get("thumbnailPath").getAsString());
        return subject;
      }
    } catch (JsonSyntaxException e) {
      return null;
    }
    return null;
  }


  /**
   * Returns a JSON formatted string containing the properties of a given class
   * as well as the name of the class.
   * @param obj DESCRIPTION
   * @return    DESCRIPTION
   */
  public String packageClass(Object obj) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(obj);
    jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
    return gson.toJson(jsonElement);
  }

  public void stopHeartbeat() {
    this.heartbeat.stopHeartbeat();
  }

  public void listenForAccount(Account account) throws IOException {
    JsonObject jsonObject = listenForJson();

    try {
      String action = jsonObject.get("Class").getAsString();

      if (action.equals("Account")) {
        try {
          account.setEmailAddress(jsonObject.get("emailAddress").getAsString());
          account.setTutorStatus(jsonObject.get("tutorStatus").getAsInt());
        } catch (NullPointerException e) {
          System.out.println("Failed by Credentials");
        }
      }
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
  }
}
