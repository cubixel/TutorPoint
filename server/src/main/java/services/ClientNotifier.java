package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.response.LiveTutorHomeWindowUpdate;
import model.response.SubjectHomeWindowResponse;
import model.response.TopTutorHomeWindowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;


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

  /**
   * Based on the number of subjects already sent this method gets the next five
   * subject from the database and creates a new Subject class which is then
   * packaged as a json and written to the DataOutputStream. If five more
   * subjects are not available it sends as many as it can and then breaks out
   * the loop. Each Subject is preceded by a String with the state of that
   * request.
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
  public void sendSubjects(MySql sqlConnection, int numberOfSubjectsSent, String subject, int userID) {
    log.info("sendingSubjects");
    int subjectID;
    String subjectName;
    String category;
    boolean subjectFollowed;
    ResultSet resultSet;
    int subjectsToSend = 5;

    // Get the next subject from the MySQL database.
    try {
      if (subject != null) {
        resultSet = sqlConnection.getSubjects(
            sqlConnection.getCategoryID(
                sqlConnection.getSubjectCategory(
                    sqlConnection.getSubjectID(subject))));
      } else {
        resultSet = sqlConnection.getSubjects();
      }

      for (int i = 0; i < numberOfSubjectsSent; i++) {
        resultSet.next();
      }

      int subjectCounter = 0;
      while (subjectCounter < subjectsToSend) {
        // Assigning values to fields from database result.
        if (resultSet.next()) {
          // Creating a Subject object which is packaged as a json and sent on the dos.
          subjectID = resultSet.getInt("subjectID");
          subjectName = resultSet.getString("subjectname");
          category = sqlConnection.getSubjectCategory(subjectID);
          subjectFollowed = sqlConnection.isSubjectFollowed(subjectID, userID);
          sendString(packageClass(new SubjectHomeWindowResponse(subjectID, subjectName,
              category, subjectFollowed)));
          subjectCounter++;
        } else {
          subjectCounter = subjectsToSend;
        }
      }
    } catch (SQLException e) {
      log.error("SendSubjects error accessing database ", e);
    }
  }

  /**
   *
   * @param sqlConnection
   *        The Class that connects to the MySQL Database
   *
   * @param numberOfTutorsSent
   *        The number of tutors already sent to the Client
   *
   */
  public void sendTopTutors(MySql sqlConnection,
    int numberOfTutorsSent, int userID) {
    // Creating temporary fields
    int tutorID;
    float rating;
    String username;
    int tutorsToSend = 5;
    boolean tutorFollowed;

    try {
      ResultSet resultSet = sqlConnection.getTutorsDescendingByAvgRating();
      for (int i = 0; i < numberOfTutorsSent; i++) {
        resultSet.next();
      }

      int tutorCounter = 0;
      while (tutorCounter < tutorsToSend) {
        if (resultSet.next()) {
          tutorID = resultSet.getInt("tutorID");
          rating = resultSet.getFloat("rating");
          username = sqlConnection.getUsername(tutorID);
          tutorFollowed = sqlConnection.isTutorFollowed(tutorID, userID);
          sendString(packageClass((new TopTutorHomeWindowResponse(username, tutorID,
              rating, tutorFollowed))));
          tutorCounter++;
        } else {
          tutorCounter = tutorsToSend;
        }
      }
    } catch (SQLException e) {
      log.error("SendTopTutors error accessing database ", e);
    }
  }

  /**
   *
   * @param sqlConnection
   *        The Class that connects to the MySQL Database
   *
   * @throws SQLException
   *         If failure to access MySQL database.
   */
  public void sendLiveTutors(MySql sqlConnection, int userID) {
    int tutorID;
    float rating;
    String username;
    boolean isLive;
    try {
      ResultSet resultSet = sqlConnection.getFollowedTutors(userID);
      while (resultSet.next()) {
        tutorID = resultSet.getInt("tutorID");
        rating = sqlConnection.getTutorsRating(tutorID, userID);
        username = sqlConnection.getUsername(tutorID);
        isLive = sqlConnection.isTutorLive(tutorID);
        sendString(packageClass((new LiveTutorHomeWindowUpdate(username, tutorID, rating, isLive))));
      }
    } catch (SQLException e) {
      log.error("SendLiveTutors, error accessing database ", e);
    }
  }

  public DataInputStream getDataInputStream() {
    return dis;
  }

  public DataOutputStream getDataOutputStream() {
    return dos;
  }

}