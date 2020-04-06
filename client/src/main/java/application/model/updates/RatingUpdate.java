package application.model.updates;

public class RatingUpdate {
  int rating;
  int userID;
  int tutorID;

  public RatingUpdate(int rating, int userID, int tutorID) {
    this.rating = rating;
    this.userID = userID;
    this.tutorID = tutorID;
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public int getTutorID() {
    return tutorID;
  }

  public void setTutorID(int tutorID) {
    this.tutorID = tutorID;
  }
}
