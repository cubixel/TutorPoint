package application.model.requests;

public class TopTutorsRequest {
  int numberOfTutorsRequested;
  int userID;

  public TopTutorsRequest(int numberOfTutorsRequested, int userID) {
    this.numberOfTutorsRequested = numberOfTutorsRequested;
    this.userID = userID;
  }
}
