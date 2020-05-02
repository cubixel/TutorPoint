package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Account;
import application.model.Message;
import application.model.Subject;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION. #################
 *
 * @author Che McKirgan
 * @author James Gardner
 * @author Daniel Bishop
 */
public class MainConnection extends Thread {
  private Socket socket = null;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Heartbeat heartbeat;
  private ListenerThread listener;
  private int userID;
  private int token;
  private boolean inUse;
  private static final Logger log = LoggerFactory.getLogger("MainConnection");

  /**
   * Constructor that creates a socket of a specific IP Address and Port Number.
   * And sets up data input and output streams on that socket.
   *
   * @param connectionAdr IP Address for Connection.
   * @param port          Port Number.
   */
  public MainConnection(String connectionAdr, int port) throws IOException {

    setDaemon(true);
    setName("Watchdog");

    /* If the connection address is null then it will default to localhost. */
    if (connectionAdr == null) {
      connectionAdr = "localhost";
    }
    socket = new Socket(connectionAdr, port);
    log.info("Connecting to Address '" + connectionAdr + "' on Port: '" + port + "'");

    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());

    token = dis.readInt();
    log.info("Recieved token " + token);

    listener = new ListenerThread(connectionAdr, port + 1, token);
    log.info("Spawned ListenerThread");
    listener.start();

    heartbeat = new Heartbeat(this);
    heartbeat.start();
  }

  /**
   * This is a constructor just used for testing the MainConnection Class.
   * 
   * @param dis A DataInputStream for the MainConnection to receive data.
   * @param dos A DataOutputStream for the MainConnection to send data.
   */
  public MainConnection(DataInputStream dis, DataOutputStream dos, Heartbeat heartbeat) {
    setDaemon(true);
    setName("Watchdog");
    this.dis = dis;
    this.dos = dos;
    this.heartbeat = heartbeat;
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
   * Listens for incoming data. Timeout of 3s after which a network failure error
   * is returned.
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
   * METHOD DESCRIPTION.
   */
  public File listenForFile() throws IOException {

    // TODO handle the exceptions better as it just throws a generic IOException.
    int bytesRead;

    String fileName = dis.readUTF();
    long size = dis.readLong();
    log.info("Listening for file named '" + fileName + "' of size " + size);
    OutputStream output =
        new FileOutputStream("client/src/main/resources/application/media/downloads/"
            + fileName);
    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    output.close();

    return new File("client/src/main/resources/application/media/downloads/" + fileName);
  }

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

//  /**
//   * ] * Listens for a string on the dis and then * attempts to create a subject
//   * object from the * json string.
//   *
//   * @return The Subject sent from the server.
//   * @throws IOException No String on DIS.
//   */
//  public Subject listenForSubject() throws IOException {
//
//    JsonObject jsonObject = listenForJson();
//    Subject subject;
//    try {
//      String action = jsonObject.get("Class").getAsString();
//
//      if (action.equals("Subject")) {
//        subject =
//            new Subject(jsonObject.get("id").getAsInt(), jsonObject.get("name").getAsString(),
//                jsonObject.get("category").getAsString());
//        return subject;
//      }
//    } catch (JsonSyntaxException e) {
//      log.error("Json Error", e);
//      return null;
//    }
//    return null;
//  }

  /**
   * Returns a JSON formatted string containing the properties of a given class as
   * well as the name of the class.
   * 
   * @param obj DESCRIPTION
   * @return DESCRIPTION
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
   * .
   */
  public Account listenForAccount() throws IOException {
    JsonObject jsonObject = listenForJson();
    Account account;

    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "uploaded"
        + File.separator + "profilePictures" + File.separator;

    try {
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
          // create a image
          Image profileImage = new Image(input);
          account.setProfilePicture(profileImage);
        } catch (FileNotFoundException fnfe) {
          log.warn("Account " + jsonObject.get("username").getAsString() + " has no profile picture");
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
   * Attempts to claim the mainconnection.
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

}
