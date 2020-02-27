package services;

import java.io.DataOutputStream;
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

  public void getFiveSubjects(){
    sqlConnection.getNextFiveSubjects(numberOfSubjectSent);
  }

  public int getNumberOfSubjectSent() {
    return numberOfSubjectSent;
  }

  public void setNumberOfSubjectSent(int numberOfSubjectSent) {
    this.numberOfSubjectSent = numberOfSubjectSent;
  }
}
