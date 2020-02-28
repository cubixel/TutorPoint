package services;

import static services.ServerTools.packageClass;
import static services.ServerTools.sendFileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Subject;
import sql.MySQL;

public class SubjectRequestService {
  private DataOutputStream dos;
  private int numberOfSubjectSent;
  private MySQL sqlConnection;

  public SubjectRequestService(DataOutputStream dos, MySQL sqlConnection) {
    this.dos = dos;
    this.sqlConnection = sqlConnection;
    numberOfSubjectSent = 0;
  }

  public void getSubject() {
    int id;
    String subjectName;
    String thumbnailPath;
    try {
      ResultSet resultSet = sqlConnection.getNextSubjects(numberOfSubjectSent);
      resultSet.next();
      id = resultSet.getInt("id");
      subjectName = resultSet.getString("subjectname");
      thumbnailPath = resultSet.getString("thumbnailpath");
      dos.writeUTF(packageClass((new Subject(id, subjectName, thumbnailPath))));
      numberOfSubjectSent += 1;

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
