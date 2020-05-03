package application.model.requests;

public class SubjectRequest {
  int numberOfSubjectsRequested;
  boolean requestBasedOnCategory;
  String subject;
  int userID;
  String clientWindowRequestingSubjects;

  public SubjectRequest(int numberOfSubjectsRequested, int userID) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.userID = userID;
    requestBasedOnCategory = false;
  }

  public SubjectRequest(int numberOfSubjectsRequested, String subject, int userID) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.subject = subject;
    this.userID = userID;
    requestBasedOnCategory = true;
  }

  public int getNumberOfSubjectsRequested() {
    return numberOfSubjectsRequested;
  }

  public void setNumberOfSubjectsRequested(int numberOfSubjectsRequested) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
  }
}
