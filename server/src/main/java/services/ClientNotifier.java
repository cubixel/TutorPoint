package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientNotifier {
  // private DataInputStream dis;
  private DataOutputStream dos;

  private static final Logger log = LoggerFactory.getLogger("ClientNotifier");

  public ClientNotifier(DataInputStream dis, DataOutputStream dos) {
    // this.dis = dis;
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


}