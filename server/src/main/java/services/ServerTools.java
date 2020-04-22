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
  public static void getNextFiveSubjectService(DataOutputStream dos, MySql sqlConnection,
      int numberOfSubjectsSent, String subject) throws SQLException {
    // Creating temporary fields
    int id;
    String subjectName;
    Gson gson = new Gson();
    String category;
    ResultSet resultSet;

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
      while (subjectCounter < 5) {
        // Assigning values to fields from database result.
        if (resultSet.next()) {
          // Creating a Subject object which is packaged as a json and sent on the dos.
          id = resultSet.getInt("subjectID");
          subjectName = resultSet.getString("subjectname");
          category = sqlConnection.getSubjectCategory(id);
          // sending success string
          JsonElement jsonElement = gson.toJsonTree(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          dos.writeUTF(packageClass((
              new Subject(id, subjectName, category))));
          subjectCounter++;
        } else {
          JsonElement jsonElement
              = gson.toJsonTree(SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS);
          dos.writeUTF(gson.toJson(jsonElement));
          subjectCounter = 5;
        }
      }
    } catch (IOException e) {
      log.error("ServerTools: getSubjectService, error writing to DataOutputStream ", e);
    }
  }

  /**
   *.
   * @param dos
   *        The DataOutputStream to write the Subjects too
   *
   * @param sqlConnection
   *        The Class that connects to the MySQL Database
   *
   * @param numberOfTutorsSent
   *        The number of tutors already sent to the Client
   *
   * @throws SQLException
   *         If failure to access MySQL database.
   */
  public static void getTopTutorsService(DataOutputStream dos, MySql sqlConnection,
      int numberOfTutorsSent) throws SQLException {
    // Creating temporary fields
    int tutorID;
    float rating;
    String username;
    Gson gson = new Gson();

    // Get the next subject from the MySQL database.
    try {
      ResultSet resultSet = sqlConnection.getTutorsDescendingByAvgRating();
      for (int i = 0; i < numberOfTutorsSent; i++) {
        resultSet.next();
      }

      int tutorCounter = 0;
      while (tutorCounter < 5) {
        // Assigning values to fields from database result.
        if (resultSet.next()) {
          // Creating a Subject object which is packaged as a json and sent on the dos.
          tutorID = resultSet.getInt("tutorID");
          rating = resultSet.getFloat("rating");
          username = sqlConnection.getUsername(tutorID);
          // sending success string
          JsonElement jsonElement = gson.toJsonTree(TutorRequestResult.TUTOR_REQUEST_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          dos.writeUTF(packageClass((new Account(username, tutorID, rating))));
          tutorCounter++;
        } else {
          JsonElement jsonElement = gson.toJsonTree(TutorRequestResult.FAILED_BY_NO_MORE_TUTORS);
          dos.writeUTF(gson.toJson(jsonElement));
          tutorCounter = 5;
        }
      }
    } catch (IOException e) {
      log.error("ServerTools: getSubjectService, error writing to DataOutputStream ", e);
    }
  }

  /**
   *.
   * @param dos
   *        The DataOutputStream to write the Subjects too
   *
   * @param sqlConnection
   *        The Class that connects to the MySQL Database
   *
   * @param numberOfTutorsSent
   *        The number of tutors already sent to the Client
   *
   * @throws SQLException
   *         If failure to access MySQL database.
   */
  public static void getLiveTutors(DataOutputStream dos, MySql sqlConnection,
      int numberOfTutorsSent, int userID) throws SQLException {
    // Creating temporary fields
    int tutorID;
    float rating;
    String username;
    Gson gson = new Gson();

    // Get the next subject from the MySQL database.
    try {
      ResultSet resultSet = sqlConnection.getLiveTutors(userID);
      for (int i = 0; i < numberOfTutorsSent; i++) {
        resultSet.next();
      }

      int tutorCounter = 0;
      while (tutorCounter < 5) {
        // Assigning values to fields from database result.
        if (resultSet.next()) {
          // Creating a Subject object which is packaged as a json and sent on the dos.
          tutorID = resultSet.getInt("tutorID");
          rating = sqlConnection.getTutorsRating(tutorID, userID);
          username = sqlConnection.getUsername(tutorID);
          // sending success string
          JsonElement jsonElement = gson.toJsonTree(TutorRequestResult.TUTOR_REQUEST_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          dos.writeUTF(packageClass((new Account(username, tutorID, rating))));
          tutorCounter++;
        } else {
          JsonElement jsonElement = gson.toJsonTree(TutorRequestResult.FAILED_BY_NO_MORE_TUTORS);
          dos.writeUTF(gson.toJson(jsonElement));
          tutorCounter = 5;
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
