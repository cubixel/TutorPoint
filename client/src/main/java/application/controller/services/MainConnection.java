package application.controller.services;

import application.controller.MainWindowController;
import application.model.Account;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main connection between client and server. It is the
 * first connection setup during boot of the program and determines
 * if the client can start correctly. It contains methods for
 * communicating between server and client.
 *
 * @author Che McKirgan
 * @author James Gardner
 * @author Daniel Bishop
 *
 * @see Heartbeat
 * @see ListenerThread
 */
public class MainConnection extends Thread {

  private final DataInputStream dis;
  private final DataOutputStream dos;
  private final Heartbeat heartbeat;
  private ListenerThread listener;
  private int userID;
  private boolean inUse;
  private MainWindowController mainWindow;
  private static final Logger log = LoggerFactory.getLogger("MainConnection");

  /**
   * Constructor that creates a socket of a specific IP Address and Port Number.
   * And sets up data input and output streams on that socket.
   *
   * @param connectionAdr
   *        IP Address for connection, defaults to localhost if null
   *
   * @param port
   *        Port number
   */
  public MainConnection(String connectionAdr, int port) throws IOException {

    setDaemon(true);
    setName("Watchdog");

    /* If the connection address is null then it will default to localhost. */
    if (connectionAdr == null) {
      connectionAdr = "localhost";
    }
    Socket socket = new Socket(connectionAdr, port);
    log.info("Connecting to Address '" + connectionAdr + "' on Port: '" + port + "'");

    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());

    int token = dis.readInt();
    log.info("Received token " + token);

    listener = new ListenerThread(connectionAdr, port + 1, token);
    log.info("Spawned ListenerThread");
    listener.start();

    heartbeat = new Heartbeat(this);
    heartbeat.start();
  }

  /**
   * This is a constructor just used for testing the MainConnection Class.
   *
   * @param dis
   *        A DataInputStream for the MainConnection to receive data
   *
   * @param dos
   *        A DataOutputStream for the MainConnection to send data
   */
  public MainConnection(DataInputStream dis, DataOutputStream dos, Heartbeat heartbeat) {
    setDaemon(true);
    setName("Watchdog");
    this.dis = dis;
    this.dos = dos;
    this.heartbeat = heartbeat;
    heartbeat.start();
  }

  /**
   * Takes a String as an input and sends it to the server.
   */
  public void sendString(String input) throws IOException {
    // if (!input.equals("Heartbeat")) {
    //   log.info("Sent: " + input);
    // }
    dos.writeUTF(input);
  }

  /**
   * Listens for incoming data. Timeout of 10s after which a network failure error
   * is returned.
   *
   * @return {@code String} result if successful and {@code FAILED_BY_NETWORK} if not
   *
   * @throws IOException
   *         Thrown if error reading from DataInputStream
   */
  public String listenForString() throws IOException {
    String incoming = null;
    long startTime = System.currentTimeMillis();
    boolean received = false;

    while ((System.currentTimeMillis() - startTime) <= 10000 && !received) {
      if (dis.available() > 0) {
        incoming = dis.readUTF();
        received = true;
      }
    }
    log.info("Server Reply: " + incoming);
    return Objects.requireNonNullElse(incoming, "FAILED_BY_NETWORK");
  }

  /**
   * Listens for a file from the server. Initially the server will send a {@code String}
   * containing the name of the file, then a {@code long} with the file size. Then it
   * sends the file as a stream of bytes that are used to construct the file.
   *
   * @return {@code File} result if successful
   *
   * @throws IOException
   *         Thrown if error reading from DataInputStream or creating File
   */
  public File listenForFile() throws IOException {
    // TODO handle the exceptions better as it just throws a generic IOException.
    int bytesRead;

    String fileName = dis.readUTF();
    long size = dis.readLong();
    log.info("Listening for file named '" + fileName + "' of size " + size);

    String path = "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "application" + File.separator
        + "media" + File.separator + "downloads" + File.separator;

    OutputStream output = new FileOutputStream(path + fileName);

    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    output.close();

    return new File(path + fileName);
  }

  /**
   * Used to send files to the server.
   *
   * @param file
   *        The file to be sent to the server
   *
   * @throws IOException
   *         Thrown if error reading from DataOutputStream or writing File
   */
  public void sendFile(File file) throws IOException {
    byte[] byteArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    dis.readFully(byteArray, 0, byteArray.length);
    log.info("Sending filename '" + file.getName() + "' of size " + byteArray.length);
    dos.writeUTF(file.getName());
    dos.writeLong(byteArray.length);
    dos.write(byteArray, 0, byteArray.length);
    dos.flush();
    dis.close();
  }

  private JsonObject listenForJson() throws IOException {
    String serverReply = this.listenForString();

    Gson gson = new Gson();
    try {
      if (serverReply.equals("FAILED_BY_NETWORK")) {
        return null;
      } else {
        return gson.fromJson(serverReply, JsonObject.class);
      }
    } catch (JsonSyntaxException e) {
      log.error("ListenForJson, ServerReply = " + serverReply);
      log.error("Was expecting an Account", e);
      return null;
    }
  }

  /**
   * Returns a JSON formatted string containing the properties of a given class as
   * well as the name of the class.
   *
   * @param obj
   *        The object to be packaged as a Json
   *
   * @return {@code JsonElement} version of the object sent in
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

  /**
   * Listens for Account information, used for both logging in a user
   * and also recieving account information for other accounts such
   * as Tutors. Unpacks the jsonObject that has class of Account and
   * builds a new Account class.
   *
   * @return {@code Account} if successful or {@code Null} if not
   *
   * @throws IOException
   *         Thrown if error converting incoming string to Json
   */
  public Account listenForAccount() throws IOException {
    JsonObject jsonObject = listenForJson();
    Account account;

    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded"
        + File.separator + "profilePictures" + File.separator;

    try {
      assert jsonObject != null;
      String action = jsonObject.get("Class").getAsString();

      if (action.equals("Account")) {
        account = new Account(jsonObject.get("userID").getAsInt(),
            jsonObject.get("username").getAsString(),
            jsonObject.get("emailAddress").getAsString(),
            jsonObject.get("hashedpw").getAsString(),
            jsonObject.get("tutorStatus").getAsInt(), 0);

        JsonArray jsonArray = jsonObject.getAsJsonArray("followedSubjects");
        for (int i = 0; i < jsonArray.size(); i++) {
          account.addFollowedSubjects(jsonArray.get(i).getAsString());
        }

        try {
          FileInputStream input = new FileInputStream(path + "user"
              + jsonObject.get("userID").getAsInt() + "profilePicture.png");
          /* Create a image used for profile picture */
          Image profileImage = new Image(input);
          account.setProfilePicture(profileImage);
        } catch (FileNotFoundException fnfe) {
          log.warn("Account " + jsonObject.get("username").getAsString()
              + " has no profile picture");
        }
        return account;
      }
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ListenerThread getListener() {
    return listener;
  }

  /**
   * Attempts to claim the MainConnection.
   *
   * @return True if successful, else false.
   */
  public boolean claim() {
    if (inUse) {
      return false;
    } else {
      inUse = true;
      return true;
    }
  }

  public void release() {
    inUse = false;
  }

  @Override
  public void run() {
    int bytesAvailable = 0;
    int previousAvailable = 0;
    while (true) {
      try {
        bytesAvailable = dis.available();
      } catch (IOException e) {
        log.error("Failed to get available bytes", e);
      }

      if (bytesAvailable > 0) {
        log.info(bytesAvailable + " bytes in dis");

        if (previousAvailable == bytesAvailable) {
          String discarded = null;
          try {
            discarded = dis.readUTF();
            log.warn("Removed abandoned string: " + discarded);
          } catch (IOException e) {
            log.error("Failed to read abandoned data", e);
          }

        }
      }

      previousAvailable = bytesAvailable;
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public void setMainWindow(MainWindowController controller) {
    this.mainWindow = controller;
  }

  public MainWindowController getMainWindow() {
    return mainWindow;
  }
}