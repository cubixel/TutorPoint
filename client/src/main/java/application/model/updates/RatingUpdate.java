package application.model.updates;

public class RatingUpdate {
  int rating;
  int tutorID;

  public RatingUpdate(int rating, int tutorID) {
    this.rating = rating;
    this.tutorID = tutorID;
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
