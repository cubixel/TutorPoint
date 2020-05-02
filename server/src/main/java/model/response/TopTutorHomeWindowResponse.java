package model.response;

public class TopTutorHomeWindowResponse {
  String tutorName;
  int tutorID;
  float rating;
  boolean isFollowed;

  public TopTutorHomeWindowResponse(String tutorName, int tutorID, float rating,
      boolean isFollowed) {
    this.tutorName = tutorName;
    this.tutorID = tutorID;
    this.rating = rating;
    this.isFollowed = isFollowed;
  }
}
