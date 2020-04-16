package model.requests;

public class SubjectRequest {
  int numberOfSubjectsRequested;
  boolean requestBasedOnCategory;
  String subject;

  public SubjectRequest(int numberOfSubjectsRequested) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    requestBasedOnCategory = false;
  }

  public SubjectRequest(int numberOfSubjectsRequested, String subject) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.subject = subject;
    requestBasedOnCategory = true;
  }

  public int getNumberOfSubjectsRequested() {
    return numberOfSubjectsRequested;
  }

  public void setNumberOfSubjectsRequested(int numberOfSubjectsRequested) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
  }
}
