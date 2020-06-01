package application.model.updates;

/**
 * Used to update a users rating of a tutor account. Contains all the information
 * needed for that update. This is then packaged and sent to the server.
 *
 * @author James Gardner
 */
public class RatingUpdate {
  int rating;
  int userID;
  int tutorID;

  /**
   * Constructor for the RatingUpdate Class.
   *
   * @param rating
   *        A rating from 0 to 5 for the tutor
   *
   * @param userID
   *        The ID of the user supplying the rating
   *
   * @param tutorID
   *        The ID of the tutor the user is rating
   */
  public RatingUpdate(int rating, int userID, int tutorID) {
    this.rating = rating;
    this.userID = userID;
    this.tutorID = tutorID;
  }
}