package services;

import static services.ServerTools.packageClass;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Subject;
import sql.MySQL;

public class SubjectRequestService {
  private DataOutputStream dos;
  private int numberOfSubjectsSent;
  private MySQL sqlConnection;

  /**
   * Constructor the SubjectRequestService.
   *
   * @param dos The DataOutputStream the return json string will be sent on.
   * @param sqlConnection The connection to the MySQL database.
   */
  public SubjectRequestService(DataOutputStream dos, MySQL sqlConnection) {
    this.dos = dos;
    this.sqlConnection = sqlConnection;
    numberOfSubjectsSent = 0;
  }

  /**
   * Based on the number of previous requests gets the next subject from the
   * database and creates a new Subject class which is then packaged as a json
   * and written to the DataOutputStream. numberOfSubjectsSent is then incremented.
   */
  public void getSubject() {
    // Creating temporary fields
    int id;
    String subjectName;
    String nameOfThumbnailFile;
    String thumbnailPath;

    // Get the next subject from the MySQL database.
    try {
      ResultSet resultSet = sqlConnection.getNextSubjects(numberOfSubjectsSent);
      resultSet.next();

      // Assigning values to fields from database result.
      id = resultSet.getInt("id");
      subjectName = resultSet.getString("subjectname");
      thumbnailPath = resultSet.getString("thumbnailpath");
      nameOfThumbnailFile = resultSet.getString("filename");

      // Creating a Subject object which is packaged as a json and sent on the dos.
      dos.writeUTF(packageClass((new Subject(id, subjectName, nameOfThumbnailFile, thumbnailPath))));

      numberOfSubjectsSent += 1;

    } catch (SQLException | IOException e) {
      e.printStackTrace();
      // TODO Deal with these errors.
    }
  }

  public int getNumberOfSubjectsSent() {
    return numberOfSubjectsSent;
  }
}
