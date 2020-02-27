package services;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.MySQL;

public class SubjectRequestService {
  private DataOutputStream dos;
  private int numberOfSubjectSent;
  private MySQL sqlConnection;
  private SendFileService sendFileService;

  public SubjectRequestService(DataOutputStream dos, MySQL sqlConnection) {
    this.dos = dos;
    this.sqlConnection = sqlConnection;
    numberOfSubjectSent = 0;
  }

  public void getFiveSubjects() {
    String subjectName;
    String thumbnailPath;
    try {
      for (int i = 0; i < 1; i++) {
        ResultSet resultSet = sqlConnection.getNextSubjects(numberOfSubjectSent);
        resultSet.next();
        subjectName = resultSet.getString("subjectname");
        thumbnailPath = resultSet.getString("thumbnailpath");
        dos.writeUTF(subjectName);
        File thumbnail = new File(thumbnailPath);
        sendFileService = new SendFileService(dos, thumbnail);
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  public int getNumberOfSubjectSent() {
    return numberOfSubjectSent;
  }

  public void setNumberOfSubjectSent(int numberOfSubjectSent) {
    this.numberOfSubjectSent = numberOfSubjectSent;
  }
}
