package application.model.requests;

public class SubjectRequestSubscription {
  int numberOfSubjectsRequested;
  int userID;
  String subject;

  public SubjectRequestSubscription(int numberOfSubjectsRequested, int userID, String subject) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.userID = userID;
    this.subject = subject;
  }
}
