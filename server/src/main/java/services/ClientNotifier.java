package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientNotifier {
  private DataInputStream dis;
  private DataOutputStream dos;

  private static final Logger log = LoggerFactory.getLogger("ClientNotifier");

  public ClientNotifier(DataInputStream dis, DataOutputStream dos) {
    this.dis = dis;
    this.dos = dos;
  }

  /**
   * Sends a JSON formatted string containing the properties of a given class as
   * well as the name of the class to the associated client.
   * 
   * @param obj The Object to send
   * @return True if successfully sent, else false
   */
  public boolean sendClass(Object obj) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(obj);
    log.debug(obj.getClass().getSimpleName());
    jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());

    try {
      log.debug(jsonElement.toString());
      dos.writeUTF(gson.toJson(jsonElement));
    } catch (IOException e) {
      log.error("Failed to send '" + obj.getClass().getSimpleName() + "'' class", e);
      return false;
    }
    return true;
  }

  /**
   * Listens for a file from the client.
   * 
   * @return The File uploaded
   * @throws IOException Communication Error occured
   */
  public File listenForFile(String directoryPath) throws IOException {

    // TODO handle the exceptions better as it just throws a generic IOException.
    int bytesRead;

    String fileName = dis.readUTF();
    long size = dis.readLong();
    log.info("Listening for file named '" + fileName + "' of size " + size);
    OutputStream output =
        new FileOutputStream(directoryPath + fileName);
    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    output.close();

    return new File(directoryPath + fileName);
  }

  public boolean sendJson(JsonObject jsonObject) {

    try {
      dos.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Failed to send JsonObject class", e);
      return false;
    }
    return true;
  }

  public boolean sendJsonArray(ArrayList<JsonObject> array) {
    JsonObject jsonObject = new JsonObject();

    int i = 0;
    for (JsonObject obj : array) {
      jsonObject.add("WhiteboardSession" + i, obj);
      i++;
    }
    jsonObject.addProperty("Index", i);
    jsonObject.addProperty("Class", array.getClass().getSimpleName());

    log.debug(jsonObject.toString());

    try {
      dos.writeUTF(jsonObject.toString());
    } catch (IOException e) {
      log.error("Failed to send JsonObject class", e);
      return false;
    }
    return true;
  }

  public boolean sendString(String string) {
    try {
      dos.writeUTF(string);
    } catch (IOException e) {
      log.error("Failed to send string", e);
      return false;
    }
    return true;
  }

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

  public DataInputStream getDataInputStream() {
    return dis;
  }

  public DataOutputStream getDataOutputStream() {
    return dos;
  }

}