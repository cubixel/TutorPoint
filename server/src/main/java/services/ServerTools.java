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
import model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.enums.SubjectRequestResult;
import sql.MySql;

/**
 * ServerTools contains a set of Static methods used
 * for packaging and sending data.
 *
 * @author James Gardner
 */
public final class ServerTools {

  private static final Logger log = LoggerFactory.getLogger("Server Logger");

  /**
   * METHOD DESCRIPTION.
   *
   * @param dos           DESCRIPTION
   * @param file          DESCRIPTION
   * @throws IOException  DESCRIPTION
   */
  public static void sendFileService(DataOutputStream dos, File file) throws IOException {
    byte[] byteArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    dis.readFully(byteArray, 0, byteArray.length);
    dos.writeUTF(file.getName());
    dos.writeLong(byteArray.length);
    dos.write(byteArray, 0, byteArray.length);
    dos.flush();
    dis.close();
  }


  /**
   * Based on the number of subjects already sent this method gets the next five
   * subject from the database and creates a new Subject class which is then
   * packaged as a json and written to the DataOutputStream. If five more
   * subjects are not available it sends as many as it can and then breaks out
   * the loop. Each Subject is preceded by a String with the state of that
   * request.
   *
   * @param dos
   *        The DataOutputStream to write the Subjects too
   *
   * @param sqlConnection
   *        The Class that connects to the MySQL Database
   *
   * @param numberOfSubjectsSent
   *        The number of subjects already sent to the Client
   *
   * @throws SQLException
   *         If failure to access MySQL database.
   */
  public static void getSubjectService(DataOutputStream dos, MySql sqlConnection,
      int numberOfSubjectsSent) throws SQLException {
    // Creating temporary fields
    int id;
    String subjectName;
    String nameOfThumbnailFile;
    String thumbnailPath;
    String coverPhotoPath;
    String coverPhotoFilename;
    Gson gson = new Gson();

    // Get the next subject from the MySQL database.
    try {
      ResultSet resultSet = sqlConnection.getSubjects();
      for (int i = 0; i < numberOfSubjectsSent; i++) {
        boolean result = resultSet.next();
      }

      int subjectCounter = 0;
      int i = 0;
      while (subjectCounter < 5) {
        // Assigning values to fields from database result.
        if (resultSet.next()) {
          // Creating a Subject object which is packaged as a json and sent on the dos.
          id = resultSet.getInt("subjectID");
          subjectName = resultSet.getString("subjectname");
          thumbnailPath = resultSet.getString("thumbnailpath");
          nameOfThumbnailFile = resultSet.getString("thumbnailfilename");
          coverPhotoPath = resultSet.getString("coverphotopath");
          coverPhotoFilename = resultSet.getString("coverphotofilename");
          // sending success string
          JsonElement jsonElement = gson.toJsonTree(SubjectRequestResult.SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          dos.writeUTF(packageClass((
              new Subject(id, subjectName, nameOfThumbnailFile, thumbnailPath, coverPhotoFilename, coverPhotoPath))));
          subjectCounter++;
          i++;
        } else {
          JsonElement jsonElement = gson.toJsonTree(SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS);
          dos.writeUTF(gson.toJson(jsonElement));
          subjectCounter = 5;
        }
      }
    } catch (IOException e) {
      log.error("ServerTools: getSubjectService, error writing to DataOutputStream ", e);
    }
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
