package model.response;

public class LiveTutorHomeWindowUpdate {
  private String tutorName;
  private int tutorID;
  private float rating;
  private boolean isLive;

  public LiveTutorHomeWindowUpdate(String tutorName, int tutorID, float rating, boolean isLive) {
    this.tutorName = tutorName;
    this.tutorID = tutorID;
    this.rating = rating;
    this.isLive = isLive;
  }
}
