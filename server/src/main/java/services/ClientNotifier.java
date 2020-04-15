package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
    
    try {
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

}