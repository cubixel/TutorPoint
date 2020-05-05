package application.model.requests;

public class SubjectRequestHome {
  int numberOfSubjectsRequested;
  int userID;

  public SubjectRequestHome(int numberOfSubjectsRequested, int userID) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.userID = userID;
  }
}
