package model.response;

/**
 * Used to send a tutor from the Server to the Client
 * specifically to update the home window top tutors on the
 * Client side. Contains all the information on the tutor
 * relevant to that user.
 *
 * @author James Gardner
 * @see services.ClientNotifier
 */
public class TopTutorHomeWindowResponse {
  String tutorName;
  int tutorID;
  float rating;
  boolean isFollowed;

  /**
   * Constructor for the TopTutorHomeWindowResponse all information must
   * be supplied.
   *
   * @param tutorName
   *        A string with the unique name of the tutor
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param rating
   *        The current average rating of the tutor
   *
   * @param isFollowed
   *        If the user is following the tutor or not
   */
  public TopTutorHomeWindowResponse(String tutorName, int tutorID, float rating,
      boolean isFollowed) {
    this.tutorName = tutorName;
    this.tutorID = tutorID;
    this.rating = rating;
    this.isFollowed = isFollowed;
  }
}