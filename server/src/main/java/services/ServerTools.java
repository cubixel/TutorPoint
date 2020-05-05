package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Account;
import model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.enums.LiveTutorRequestResult;
import services.enums.SubjectRequestResult;
import services.enums.TutorRequestResult;
import sql.MySql;

/**
 * ServerTools contains a set of Static methods used
 * for packaging and sending data.
 *
 * @author James Gardner
 */
public final class ServerTools {

  private static final Logger log = LoggerFactory.getLogger("ServerTools");

  /**
   * METHOD DESCRIPTION.
   *
   * @param dos           DESCRIPTION
   * @param file          DESCRIPTION
   * @throws IOException  DESCRIPTION
   */
  public static void sendFileService(DataOutputStream dos, File file) throws IOException {
    final Logger log = LoggerFactory.getLogger("SendFileLogger");

    byte[] byteArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    dis.readFully(byteArray, 0, byteArray.length);
    log.info("Sending filename '" + file.getName() + "' of size " + byteArray.length);
    dos.writeUTF(file.getName());
    log.info("" + byteArray.length);
    dos.writeLong(byteArray.length);
    dos.write(byteArray, 0, byteArray.length);
    dos.flush();
    dis.close();
  }

  /**
   * Returns a JSON formatted string containing the properties of a given class
   * as well as the name of the class.
   *
   * @param obj DESCRIPTION
   * @return    DESCRIPTION
   */
  public static String packageClass(Object obj) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(obj);
    jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
    return gson.toJson(jsonElement);
  }
}
